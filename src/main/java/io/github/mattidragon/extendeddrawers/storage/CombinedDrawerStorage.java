package io.github.mattidragon.extendeddrawers.storage;

import com.google.common.collect.Iterators;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Tweaked version of {@link net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage CombinedStorage} for the combined storage of normal drawers.
 * Stores slots in a fixed size array. The storage array is copied on iteration to avoid issues with mods that iterate storages outside transactions, because we need to sort slots.
 * This class also don't need to handle multi-view storages since each slot is a single view, so it's simpler.
 */
public class CombinedDrawerStorage implements SlottedStorage<ItemVariant> {
    private DrawerSlot[] sortedSlots;
    private final DrawerSlot[] unsortedSlots;

    public CombinedDrawerStorage(DrawerSlot[] slots) {
        this.unsortedSlots = slots;
        this.sortedSlots = Arrays.copyOf(slots, slots.length);
    }

    public void sort() {
        // We copy the slot array so that currently active iterators don't break
        sortedSlots = Arrays.copyOf(sortedSlots, sortedSlots.length);
        Arrays.sort(sortedSlots);
    }

    @Override
    public boolean supportsInsertion() {
        for (DrawerSlot part : sortedSlots) {
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

        for (DrawerSlot part : sortedSlots) {
            amount += part.insert(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public boolean supportsExtraction() {
        for (DrawerSlot part : sortedSlots) {
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

        for (DrawerSlot part : sortedSlots) {
            amount += part.extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
        return Iterators.forArray(sortedSlots);
    }

    @Override
    public int getSlotCount() {
        return unsortedSlots.length;
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return unsortedSlots[slot];
    }
}
