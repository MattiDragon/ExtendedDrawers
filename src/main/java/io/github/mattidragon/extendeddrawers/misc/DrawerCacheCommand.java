package io.github.mattidragon.extendeddrawers.misc;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.util.NodePos;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.cache.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.network.node.CompactingDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerBlockNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class DrawerCacheCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("drawercache")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                            .then(CommandManager.literal("print")
                                    .executes(context -> {
                                        var source = context.getSource();
                                        var pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                        NetworkRegistry.UNIVERSE.getGraphWorld(source.getWorld())
                                                .getAllGraphsAt(pos)
                                                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                .map(NetworkStorageCache::getDebugInfo)
                                                .flatMap(List::stream)
                                                .forEach(line -> source.sendFeedback(() -> line, false));
                                        return 1;
                                    }))
                            .then(CommandManager.literal("check")
                                    .executes(context -> {
                                        var source = context.getSource();
                                        var pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                        var graphs = NetworkRegistry.UNIVERSE.getGraphWorld(source.getWorld())
                                                .getAllGraphsAt(pos)
                                                .toList();
                                        if (graphs.isEmpty()) {
                                            source.sendFeedback(() -> Text.literal("No graph found").formatted(Formatting.RED), false);
                                            return 0;
                                        }
                                        if (graphs.size() > 1) {
                                            source.sendFeedback(() -> Text.literal("Multiple graphs found").formatted(Formatting.RED), false);
                                            return 0;
                                        }

                                        var graph = graphs.getFirst();
                                        var cache = graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE);

                                        graph.getNodes()
                                                .filter(node -> node.getNode() instanceof DrawerBlockNode || node.getNode() instanceof CompactingDrawerBlockNode)
                                                .map(NodeHolder::getPos)
                                                .map(NodePos::pos)
                                                .forEach(nodePos -> {
                                                    source.sendFeedback(() -> Text.literal("Node at " + nodePos.toShortString() + ": ").formatted(Formatting.YELLOW).append(cache.getDebugInfo(nodePos)), false);
                                                });

                                        source.sendFeedback(() -> Text.literal("Checked cache").formatted(Formatting.GREEN), false);
                                        return 1;
                                    }))
                            .then(CommandManager.literal("update")
                                    .executes(context -> {
                                        var source = context.getSource();
                                        var pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                        var caches = NetworkRegistry.UNIVERSE.getGraphWorld(source.getWorld())
                                                .getAllGraphsAt(pos)
                                                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                .toList();

                                        caches.forEach(NetworkStorageCache::update);

                                        source.sendFeedback(() -> Text.literal("Updated cache").formatted(Formatting.GREEN), false);
                                        return 1;
                                    })
                                    .then(CommandManager.literal("force")
                                            .executes(context -> {
                                                var source = context.getSource();
                                                var pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                                var caches = NetworkRegistry.UNIVERSE.getGraphWorld(source.getWorld())
                                                        .getAllGraphsAt(pos)
                                                        .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                        .toList();

                                                caches.forEach(NetworkStorageCache::forceUpdate);

                                                source.sendFeedback(() -> Text.literal("Force updated cache").formatted(Formatting.GREEN), false);
                                                return 1;
                                            })))));
        });
    }
}
