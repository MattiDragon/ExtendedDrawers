package io.github.mattidragon.extendeddrawers.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public abstract class StorageDrawerBlockEntity extends BlockEntity implements StorageProvidingDrawerBlockEntity {
    public StorageDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void onSlotChanged(boolean sortingChanged) {
        DrawerBlockEntityUtils.handleSlotChanged(sortingChanged, world, pos);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        DrawerBlockEntityUtils.handleRemoved(world, pos);
    }

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        DrawerBlockEntityUtils.handleRemovalCancelled(world, pos);
    }
}
