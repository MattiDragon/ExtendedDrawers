package io.github.mattidragon.extendeddrawers.extensions.storage;

import io.github.mattidragon.extendeddrawers.extensions.block.entity.DrawerBarrelBlockEntity;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class DrawerBarrelStorage implements DrawerStorage, SlottedStorage<ItemVariant> {
    private final InventoryStorage delegate;

    public DrawerBarrelStorage(DrawerBarrelBlockEntity blockEntity) {
        this.delegate = InventoryStorage.of(blockEntity, null);
    }

    @Override
    public boolean isBlank() {
        for (var view : delegate) {
            if (!view.isResourceBlank()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public boolean isVoiding() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isDuping() {
        return false;
    }

    @Override
    public boolean hasLimiter() {
        return false;
    }

    @Override
    public int getSlotCount() {
        return delegate.getSlotCount();
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return delegate.getSlot(slot);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return delegate.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return delegate.extract(resource, maxAmount, transaction);
    }

    @Override
    public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
        return delegate.iterator();
    }
}
