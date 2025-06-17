package io.github.mattidragon.extendeddrawers.storage;

import com.google.common.base.MoreObjects;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.compacting.CompressionLadder;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.component.DrawerSlotComponent;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public final class CompactingDrawerStorage extends SnapshotParticipant<CompactingDrawerStorage.Snapshot> implements DrawerStorage, SlottedStorage<ItemVariant> {
    private final CompactingDrawerBlockEntity owner;
    private final Settings settings;
    private ItemVariant item = ItemVariant.blank();
    private final Slot[] slots = { new Slot(), new Slot(), new Slot() };
    private boolean updatePending;
    private long amount;

    public CompactingDrawerStorage(CompactingDrawerBlockEntity owner) {
        this.owner = owner;
        this.settings = new Settings();
    }

    public void readComponent(DrawerSlotComponent component) {
        settings.upgrade = component.upgrade();
        settings.limiter = component.limiter();
        settings.locked = component.locked();
        settings.hidden = component.hidden();
        settings.voiding = component.voiding();
        settings.duping = component.duping();

        item = component.item();
        amount = component.amount();
        if (item.isBlank()) amount = 0;
        updatePending = true;
    }

    public DrawerSlotComponent toComponent() {
        return new DrawerSlotComponent(
                settings.upgrade,
                settings.limiter,
                settings.locked,
                settings.hidden,
                settings.voiding,
                settings.duping,
                item,
                amount
        );
    }

    @Override
    public void dumpExcess(World world, BlockPos pos, @Nullable Direction side, @Nullable PlayerEntity player) {
        if (amount > getCapacity()) {
            var slots = getSlotArray();
            // Iterate slots in reverse order
            for (int i = slots.length - 1; i >= 0; i--) {
                var slot = slots[i];
                if (slot.isBlocked()) continue;

                var toDrop = slot.getTrueAmount() - slot.getCapacity();
                ItemUtils.offerOrDropStacks(world, pos, side, player, slot.getResource(), toDrop);
                amount -= toDrop * slot.compression;
            }
        }
        update();
    }

    @Override
    public CompactingDrawerBlockEntity getOwner() {
        return owner;
    }

    public long getCapacity() {
        var config = ExtendedDrawers.CONFIG.get().storage();
        long capacity = config.compactingCapacity();
        if (config.stackSizeAffectsCapacity())
            capacity /= (long) (64.0 / item.getItem().getMaxCount());
        if (getUpgrade() != null)
            capacity = getUpgrade().modifier.applyAsLong(capacity);
        capacity *= getTotalCompression();
        capacity = Math.min(capacity, getLimiter());
        return capacity;
    }

    @Override
    public Settings settings() {
        return settings;
    }

    @Override
    public boolean isBlank() {
        return item.isBlank();
    }

    @Override
    protected Snapshot createSnapshot() {
        return new Snapshot(item, amount);
    }

    @Override
    protected void readSnapshot(Snapshot snapshot) {
        item = snapshot.item;
        amount = snapshot.amount;
        updatePending = true;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        long inserted = 0;

        for (var slot : getActiveSlotArray()) {
            inserted += slot.insert(resource, maxAmount - inserted, transaction);
            if (inserted == maxAmount) break;
        }
        if (Arrays.stream(getActiveSlotArray()).anyMatch(slot -> slot.item.equals(resource)) && settings.voiding)
            return maxAmount;
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long extracted = 0;

        for (var slot : getActiveSlotArray()) {
            extracted += slot.extract(resource, maxAmount - extracted, transaction);
            if (extracted == maxAmount) break;
        }

        return extracted;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
        return new NonEmptyIterator();
    }

    @Override
    public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
        return new StorageIterator();
    }

    @Override
    public int getSlotCount() {
        return getActiveSlotCount();
    }

    @Override
    public Slot getSlot(int index) {
        return getSlotArray()[index];
    }

    @Override
    public void setLocked(boolean locked) {
        if (!locked && amount == 0) {
            clear();
        }
        DrawerStorage.super.setLocked(locked);
    }

    @Override
    public void readData(ReadView view) {
        DrawerStorage.super.readData(view);
        item = view.read("item", ItemVariant.CODEC).orElseGet(ItemVariant::blank);
        amount = view.getLong("amount", 0);
        if (item.isBlank()) amount = 0; // Avoids dupes with drawers of removed items
        updatePending = true;
    }

    @Override
    public void writeData(WriteView view) {
        DrawerStorage.super.writeData(view);
        view.put("item", ItemVariant.CODEC, item);
        view.putLong("amount", amount);
    }

    /**
     * Returns all non-blocked slots in a new array
     */
    public Slot[] getActiveSlotArray() {
        int count = getActiveSlotCount();
        var result = new Slot[count];
        System.arraycopy(getSlotArray(), 0, result, 0, count);
        return result;
    }

    public int getActiveSlotCount() {
        var slots = getSlotArray();
        int size;
        for (size = 0; size < slots.length; size++) {
            if (slots[size].blocked) break;
        }
        return size;
    }

    public Slot[] getSlotArray() {
        if (updatePending) updateSlots();
        return slots;
    }

    private int getTotalCompression() {
        var slots = getActiveSlotArray();
        if (slots.length == 0) return 1; // Fallback in case something breaks

        return slots[slots.length-1].compression;
    }

    private void clear() {
        item = ItemVariant.blank();
        settings.sortingDirty = true;
        for (var slot : slots) {
            slot.reset(false);
        }
        updatePending = false; // No need to update since cleared slots are up-to-date
    }

    public void updateSlots() {
        for (var slot : slots) { // Disable all slots
            slot.reset(true);
        }

        var ladder = owner.getWorld() == null
                ? new CompressionLadder(List.of(new CompressionLadder.Step(item, 1)))
                : CompressionRecipeManager.of(owner.getWorld()).getLadder(item, owner.getWorld());
        var ladderSize = ladder.steps().size();
        var initialPosition = ladder.getPosition(item);
        if (initialPosition == -1) throw new IllegalStateException("Item is not on it's own recipe ladder. Did we lookup mid-reload?");

        var positions = chooseLadderPositions(ladderSize, initialPosition);
        var globalCompression = ladder.steps().get(positions[0]).size(); // Compression level of base item, divide other compression levels by this
        for (int i = 0; i < positions.length; i++) {
            var position = positions[i];
            var step = ladder.steps().get(position);
            slots[i].compression = step.size() / globalCompression;
            slots[i].item = step.item();
            slots[i].blocked = false;
        }
        var initalSlot = Stream.of(slots).filter(slot -> slot.item.equals(item)).findFirst().orElseThrow();
        item = slots[0].item; // Set the base item to the lowest item we use. We store the count as this item.
        amount *= initalSlot.compression; // If the item has moved this corrects the amount. Multiplier should be 1 when no move has happened
        updatePending = false;
    }

    /**
     * Chooses 1-3 positions close to the supplied start position. Returns 3 positions unless size is smaller than 3.
     * Handles cases where start is at the edges.
     * @param size The size of the area
     * @param start The starting position
     * @return 1-3 positions
     */
    private static int[] chooseLadderPositions(int size, int start) {
        if (size == 1) { // Small ladders: always same entries (size 3 handled by other cases)
            return new int[]{ 0 };
        }
        if (size == 2) {
            return new int[]{ 0, 1 };
        }

        // If we are at the top of one bellow, we grab entries bellow to fill the array
        if (start == size - 1) {
            return new int[]{start - 2, start - 1, start};
        }
        if (start == size - 2) {
            return new int[]{start - 1, start, start + 1};
        }

        // By default, only use entries above to avoid shifting, because the lowest item becomes the new base item
        return new int[]{ start, start + 1, start + 2 };
    }

    @Override
    protected void onFinalCommit() {
        update();
    }

    @Override
    public long getTrueAmount() {
        return amount;
    }

    protected record Snapshot(ItemVariant item, long amount) {}

    private class StorageIterator implements Iterator<StorageView<ItemVariant>> {
        private int index = slots.length - 1;

        @Override
        public boolean hasNext() {
            return index >= 0;
        }

        @Override
        public Slot next() {
            return getSlotArray()[index--];
        }
    }


    private class NonEmptyIterator implements Iterator<StorageView<ItemVariant>> {
        private int index = slots.length - 1;

        @Override
        public boolean hasNext() {
            // Loop over and check for non-empty storage
            // No need for range check as the loop doesn't do anything if index is out of bounds
            for (int i = index; i >= 0; i--) {
                if (!getSlotArray()[i].isResourceBlank()) return true;
            }

            return false;
        }

        @Override
        public Slot next() {
            for (; index >= 0; index--) {
                if (!getSlotArray()[index].isResourceBlank()) {
                    return slots[index--];
                }
            }

            throw new NoSuchElementException();
        }
    }

    public class Slot implements SingleSlotStorage<ItemVariant> {
        private int compression;
        private ItemVariant item;
        private boolean blocked;

        public Slot() {
            reset(false);
        }

        private void reset(boolean blocked) {
            compression = 1;
            item = ItemVariant.blank();
            this.blocked = blocked;
        }

        @Override
        public long insert(ItemVariant item, long maxAmount, TransactionContext transaction) {
            if (updatePending) updateSlots();
            StoragePreconditions.notBlankNotNegative(item, maxAmount);
            if (blocked) return 0;
            if (!this.item.equals(item) && !this.item.isBlank()) return 0;
            if (!ExtendedDrawers.CONFIG.get().misc().allowRecursion() && !item.getItem().canBeNested()) return 0;
            if (this.item.isBlank() && settings.locked && !settings.lockOverridden) return 0;

            if (this.item.isBlank()) { // Special case for when drawer doesn't have item
                updateSnapshots(transaction);
                CompactingDrawerStorage.this.item = item;
                updateSlots();
                // Insert into correct slot
                return CompactingDrawerStorage.this.insert(item, maxAmount, transaction);
            }

            var inserted = Math.min(maxAmount, getSpace());
            if (inserted > 0) {
                updateSnapshots(transaction);
                amount += inserted * compression;
            } else if (inserted < 0) {
                ExtendedDrawers.LOGGER.warn("Somehow inserted negative amount of items ({}) into compacting drawer, aborting. Arguments: item={} maxAmount={}. Status: compression={} item={} capacity={} amount={}", inserted, item, maxAmount, compression, this.item, getCapacity(), getTrueAmount());
                return 0;
            }

            return inserted;
        }

        @Override
        public long extract(ItemVariant item, long maxAmount, TransactionContext transaction) {
            if (updatePending) updateSlots();
            if (blocked) return 0;
            if (!this.item.equals(item)) return 0;

            var extracted = Math.min(maxAmount, getTrueAmount());
            if (extracted > 0) {
                updateSnapshots(transaction);
                amount -= extracted * compression;
            } else if (extracted < 0) {
                ExtendedDrawers.LOGGER.warn("Somehow extracted negative amount of items ({}) from compacting drawer, aborting. Arguments: item={} maxAmount={}. Status: compression={} item={} capacity={} amount={}", extracted, item, maxAmount, compression, this.item, getCapacity(), getTrueAmount());
                return 0;
            }

            if (amount == 0 && !settings.locked && !settings.duping) {
                clear();
            }

            return settings.duping ? maxAmount : extracted;
        }

        @Override
        public boolean supportsInsertion() {
            return !blocked;
        }

        @Override
        public boolean supportsExtraction() {
            return !blocked;
        }

        @Override
        public boolean isResourceBlank() {
            if (updatePending) updateSlots();
            return item.isBlank();
        }

        @Override
        public ItemVariant getResource() {
            return item;
        }

        public long getSpace() {
            return (CompactingDrawerStorage.this.getCapacity() - CompactingDrawerStorage.this.amount) / compression;
        }

        /**
         * Returns the true amount, without duping mode shenanigans
         */
        public long getTrueAmount() {
            return CompactingDrawerStorage.this.amount / compression;
        }

        @Override
        public long getAmount() {
            return settings.duping ? Long.MAX_VALUE : getTrueAmount();
        }

        @Override
        public long getCapacity() {
            return CompactingDrawerStorage.this.getCapacity() / compression;
        }

        public CompactingDrawerStorage getStorage() {
            return CompactingDrawerStorage.this;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public long getCompression() {
            return compression;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("compression", compression)
                    .add("item", item)
                    .add("blocked", blocked)
                    .toString();
        }
    }
}
