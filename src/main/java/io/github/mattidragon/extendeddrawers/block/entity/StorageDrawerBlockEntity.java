package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;

public abstract class StorageDrawerBlockEntity extends BlockEntity {
    public StorageDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void onSlotChanged(boolean sortingChanged) {
        if (world instanceof ServerWorld serverWorld) {
            // Using this instead of markDirty to handle cases where drawer is in unloaded chunks (why doesn't minecraft save in unloaded chunks?)
            world.getWorldChunk(pos).markNeedsSaving();
            UpdateHandler.scheduleUpdate(serverWorld, pos, sortingChanged ? UpdateHandler.ChangeType.CONTENT : UpdateHandler.ChangeType.COUNT);
            var state = getCachedState();
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            world.updateComparators(pos, getCachedState().getBlock());
        }
    }

    public abstract Stream<? extends DrawerStorage> streamStorages();

    public abstract boolean isEmpty();

    @Override
    public abstract void writeData(WriteView view);

    @Override
    public void markRemoved() {
        super.markRemoved();
        if (world instanceof ServerWorld serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                    .getAllGraphsAt(pos)
                    .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                    .forEach(cache -> cache.onNodeUnloaded(pos));
        }
    }

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        if (world instanceof ServerWorld serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                    .getAllGraphsAt(pos)
                    .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                    .forEach(cache -> cache.onNodeReloaded(pos));
        }
    }
}
