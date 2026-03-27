package io.github.mattidragon.extendeddrawers.misc;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.util.NodePos;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.cache.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.network.node.CompactingDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerBlockNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;

import java.util.List;

public class DrawerCacheCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("drawercache")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                            .then(Commands.literal("print")
                                    .executes(context -> {
                                        var source = context.getSource();
                                        var pos = BlockPosArgument.getBlockPos(context, "pos");
                                        NetworkRegistry.UNIVERSE.getGraphWorld(source.getLevel())
                                                .getAllGraphsAt(pos)
                                                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                .map(NetworkStorageCache::getDebugInfo)
                                                .flatMap(List::stream)
                                                .forEach(line -> source.sendSuccess(() -> line, false));
                                        return 1;
                                    }))
                            .then(Commands.literal("check")
                                    .executes(context -> {
                                        var source = context.getSource();
                                        var pos = BlockPosArgument.getBlockPos(context, "pos");
                                        var graphs = NetworkRegistry.UNIVERSE.getGraphWorld(source.getLevel())
                                                .getAllGraphsAt(pos)
                                                .toList();
                                        if (graphs.isEmpty()) {
                                            source.sendSuccess(() -> Component.literal("No graph found").withStyle(ChatFormatting.RED), false);
                                            return 0;
                                        }
                                        if (graphs.size() > 1) {
                                            source.sendSuccess(() -> Component.literal("Multiple graphs found").withStyle(ChatFormatting.RED), false);
                                            return 0;
                                        }

                                        var graph = graphs.getFirst();
                                        var cache = graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE);

                                        graph.getNodes()
                                                .filter(node -> node.getNode() instanceof DrawerBlockNode || node.getNode() instanceof CompactingDrawerBlockNode)
                                                .map(NodeHolder::getPos)
                                                .map(NodePos::pos)
                                                .forEach(nodePos -> {
                                                    source.sendSuccess(() -> Component.literal("Node at " + nodePos.toShortString() + ": ").withStyle(ChatFormatting.YELLOW).append(cache.getDebugInfo(nodePos)), false);
                                                });

                                        source.sendSuccess(() -> Component.literal("Checked cache").withStyle(ChatFormatting.GREEN), false);
                                        return 1;
                                    }))
                            .then(Commands.literal("update")
                                    .executes(context -> {
                                        var source = context.getSource();
                                        var pos = BlockPosArgument.getBlockPos(context, "pos");
                                        var caches = NetworkRegistry.UNIVERSE.getGraphWorld(source.getLevel())
                                                .getAllGraphsAt(pos)
                                                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                .toList();

                                        caches.forEach(NetworkStorageCache::update);

                                        source.sendSuccess(() -> Component.literal("Updated cache").withStyle(ChatFormatting.GREEN), false);
                                        return 1;
                                    })
                                    .then(Commands.literal("force")
                                            .executes(context -> {
                                                var source = context.getSource();
                                                var pos = BlockPosArgument.getBlockPos(context, "pos");
                                                var caches = NetworkRegistry.UNIVERSE.getGraphWorld(source.getLevel())
                                                        .getAllGraphsAt(pos)
                                                        .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                        .toList();

                                                caches.forEach(NetworkStorageCache::forceUpdate);

                                                source.sendSuccess(() -> Component.literal("Force updated cache").withStyle(ChatFormatting.GREEN), false);
                                                return 1;
                                            })))));
        });
    }
}
