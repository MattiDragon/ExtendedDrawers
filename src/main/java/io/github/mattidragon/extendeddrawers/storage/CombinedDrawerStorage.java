package io.github.mattidragon.extendeddrawers.storage;

import com.google.common.collect.Iterators;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Tweaked version of {@link net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage CombinedStorage} for the combined storage of normal drawers.
 * Stores slots in a fixed size array. The storage array is copied on iteration to avoid issues with mods that iterate storages outside transactions, because we need to sort slots.
 * This class also don't need to handle multi-view storages since each slot is a single view, so it's simpler.
 */
@SuppressWarnings("UnstableApiUsage")
public class CombinedDrawerStorage implements Storage<ItemVariant> {
    private DrawerSlot[] slots;

    public CombinedDrawerStorage(DrawerSlot[] slots) {
        this.slots = Arrays.copyOf(slots, slots.length);
    }

    public void sort() {
        // We copy the slot array so that currently active iterators don't break
        slots = Arrays.copyOf(slots, slots.length);
        Arrays.sort(slots);
    }

    @Override
    public boolean supportsInsertion() {
        for (DrawerSlot part : slots) {
            if (part.supportsInsertion()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for (DrawerSlot part : slots) {
            amount += part.insert(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public boolean supportsExtraction() {
        for (DrawerSlot part : slots) {
            if (part.supportsExtraction()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for (DrawerSlot part : slots) {
            amount += part.extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return Iterators.forArray(slots);
    }
}


