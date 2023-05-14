package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;

/**
 * A drawer block entity that gets saved to item nbt by {@link io.github.mattidragon.extendeddrawers.misc.DrawerContentsLootFunction DrawerContentsLootFunction}.
 */
public abstract class StorageDrawerBlockEntity extends BlockEntity {
    public StorageDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void onSlotChanged(boolean sortingChanged) {
        markDirty();
        if (world instanceof ServerWorld serverWorld) {
            UpdateHandler.scheduleUpdate(serverWorld, pos, sortingChanged ? UpdateHandler.ChangeType.CONTENT : UpdateHandler.ChangeType.COUNT);
            var state = getCachedState();
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    public abstract Stream<? extends DrawerStorage> streamStorages();

    public abstract boolean isEmpty();

    @Override
    public abstract void writeNbt(NbtCompound nbt);
}
