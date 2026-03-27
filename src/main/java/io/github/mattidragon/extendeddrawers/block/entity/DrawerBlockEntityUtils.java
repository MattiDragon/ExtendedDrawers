package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class DrawerBlockEntityUtils {
    public static void handleSlotChanged(boolean sortingChanged, @Nullable Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverWorld)) return;

        var state = world.getBlockState(pos);
        // Using this instead of markDirty to handle cases where drawer is in unloaded chunks (why doesn't minecraft save in unloaded chunks?)
        world.getChunkAt(pos).markUnsaved();
        UpdateHandler.scheduleUpdate(serverWorld, pos, sortingChanged ? UpdateHandler.ChangeType.CONTENT : UpdateHandler.ChangeType.COUNT);
        world.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        world.updateNeighbourForOutputSignal(pos, state.getBlock());
    }

    public static void handleRemoved(@Nullable Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverWorld)) return;

        NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                .getAllGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .forEach(cache -> cache.onNodeUnloaded(pos));
    }

    public static void handleRemovalCancelled(@Nullable Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel serverWorld)) return;

        NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                .getAllGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .forEach(cache -> cache.onNodeReloaded(pos));
    }
}
