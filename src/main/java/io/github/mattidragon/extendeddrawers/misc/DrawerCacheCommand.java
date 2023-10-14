package io.github.mattidragon.extendeddrawers.misc;

import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.NetworkStorageCache;
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
                                        NetworkRegistry.UNIVERSE.getServerGraphWorld(source.getWorld())
                                                .getAllGraphsAt(pos)
                                                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                .map(NetworkStorageCache::getDebugInfo)
                                                .flatMap(List::stream)
                                                .forEach(line -> source.sendFeedback(() -> line, false));
                                        return 1;
                                    }))
                            .then(CommandManager.literal("update")
                                    .executes(context -> {
                                        var source = context.getSource();
                                        var pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                        var caches = NetworkRegistry.UNIVERSE.getServerGraphWorld(source.getWorld())
                                                .getAllGraphsAt(pos)
                                                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                .toList();

                                        caches.forEach(NetworkStorageCache::addMissingStorages);

                                        source.sendFeedback(() -> Text.literal("Updated cache").formatted(Formatting.GREEN), false);
                                        return 1;
                                    })
                                    .then(CommandManager.literal("force")
                                            .executes(context -> {
                                                var source = context.getSource();
                                                var pos = BlockPosArgumentType.getBlockPos(context, "pos");
                                                var caches = NetworkRegistry.UNIVERSE.getServerGraphWorld(source.getWorld())
                                                        .getAllGraphsAt(pos)
                                                        .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                                                        .toList();

                                                caches.forEach(NetworkStorageCache::forceCacheUpdate);

                                                source.sendFeedback(() -> Text.literal("Force updated cache").formatted(Formatting.GREEN), false);
                                                return 1;
                                            })))));
        });
    }
}
