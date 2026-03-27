package io.github.mattidragon.extendeddrawers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class StorageDrawerBlockEntity extends BlockEntity implements StorageProvidingDrawerBlockEntity {
    public StorageDrawerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void onSlotChanged(boolean sortingChanged) {
        DrawerBlockEntityUtils.handleSlotChanged(sortingChanged, level, worldPosition);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        DrawerBlockEntityUtils.handleRemoved(level, worldPosition);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        DrawerBlockEntityUtils.handleRemovalCancelled(level, worldPosition);
    }
}
