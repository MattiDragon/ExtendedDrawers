package io.github.mattidragon.extendeddrawers.extensions.block.entity;

import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntityUtils;
import io.github.mattidragon.extendeddrawers.block.entity.StorageProvidingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.extensions.mixin.BlockEntityAccess;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.extensions.storage.DrawerBarrelStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;

public class DrawerBarrelBlockEntity extends BarrelBlockEntity implements StorageProvidingDrawerBlockEntity {
    private final DrawerBarrelStorage storage;

    static {
        ItemStorage.SIDED.registerForBlockEntity((entity, direction) -> entity.storage, ExtensionBlocks.DRAWER_BARREL_ENTITY);
    }

    public DrawerBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        ((BlockEntityAccess) this).extended_drawers_extensions$setType(ExtensionBlocks.DRAWER_BARREL_ENTITY);
        storage = new DrawerBarrelStorage(this);
    }

    @Override
    protected Text getContainerName() {
        return ExtensionBlocks.DRAWER_BARREL.getName();
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

    @Override
    public Stream<? extends DrawerStorage> streamStorages() {
        return Stream.of(storage);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        DrawerBlockEntityUtils.handleSlotChanged(false, world, pos);
    }
}
