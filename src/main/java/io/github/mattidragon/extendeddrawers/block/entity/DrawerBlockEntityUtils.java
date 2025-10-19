package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DrawerBlockEntityUtils {
    public static void handleSlotChanged(boolean sortingChanged, World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        var state = world.getBlockState(pos);
        // Using this instead of markDirty to handle cases where drawer is in unloaded chunks (why doesn't minecraft save in unloaded chunks?)
        world.getWorldChunk(pos).markNeedsSaving();
        UpdateHandler.scheduleUpdate(serverWorld, pos, sortingChanged ? UpdateHandler.ChangeType.CONTENT : UpdateHandler.ChangeType.COUNT);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        world.updateComparators(pos, state.getBlock());
    }

    public static void handleRemoved(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                .getAllGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .forEach(cache -> cache.onNodeUnloaded(pos));
    }

    public static void handleRemovalCancelled(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                .getAllGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .forEach(cache -> cache.onNodeReloaded(pos));
    }
}
