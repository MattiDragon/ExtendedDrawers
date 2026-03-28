package io.github.mattidragon.extendeddrawers.extensions.block.entity;

import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntityUtils;
import io.github.mattidragon.extendeddrawers.block.entity.StorageProvidingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.extensions.mixin.BlockEntityAccess;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.extensions.storage.DrawerBarrelStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.Stream;

public class DrawerBarrelBlockEntity extends BarrelBlockEntity implements StorageProvidingDrawerBlockEntity {
    private final DrawerBarrelStorage storage;

    static {
        ItemStorage.SIDED.registerForBlockEntity((entity, _) -> entity.storage, ExtensionBlocks.DRAWER_BARREL_ENTITY);
    }

    public DrawerBarrelBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(worldPosition, blockState);
        ((BlockEntityAccess) this).extended_drawers_extensions$setType(ExtensionBlocks.DRAWER_BARREL_ENTITY);
        storage = new DrawerBarrelStorage(this);
    }

    @Override
    protected Component getDefaultName() {
        return ExtensionBlocks.DRAWER_BARREL.getName();
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

    @Override
    public Stream<? extends DrawerStorage> streamStorages() {
        return Stream.of(storage);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        DrawerBlockEntityUtils.handleSlotChanged(false, level, worldPosition);
    }
}
