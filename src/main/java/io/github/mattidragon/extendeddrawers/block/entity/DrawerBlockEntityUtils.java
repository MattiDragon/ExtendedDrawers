package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public class DrawerBlockEntityUtils {
    public static void handleSlotChanged(boolean sortingChanged, @Nullable Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        var state = level.getBlockState(pos);
        // Using this instead of markDirty to handle cases where drawer is in unloaded chunks (why doesn't minecraft save in unloaded chunks?)
        level.getChunkAt(pos).markUnsaved();
        UpdateHandler.scheduleUpdate(serverLevel, pos, sortingChanged ? UpdateHandler.ChangeType.CONTENT : UpdateHandler.ChangeType.COUNT);
        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        level.updateNeighbourForOutputSignal(pos, state.getBlock());
    }

    public static void handleRemoved(@Nullable Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        NetworkRegistry.UNIVERSE.getGraphWorld(serverLevel)
                .getAllGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .forEach(cache -> cache.onNodeUnloaded(pos));
    }

    public static void handleRemovalCancelled(@Nullable Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        NetworkRegistry.UNIVERSE.getGraphWorld(serverLevel)
                .getAllGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .forEach(cache -> cache.onNodeReloaded(pos));
    }
}
