package io.github.mattidragon.extendeddrawers.storage;

import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.compacting.CompressionLadder;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.config.ExtendedDrawersConfig;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class CompactingDrawerStorage extends SnapshotParticipant<CompactingDrawerStorage.Snapshot> implements DrawerStorage {
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

    @Override
    public boolean changeUpgrade(@Nullable UpgradeItem newUpgrade, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player) {
        var oldUpgrade = settings.upgrade;
        settings.upgrade = newUpgrade;
        if (amount > getCapacity() && ExtendedDrawersConfig.get().misc().blockUpgradeRemovalsWithOverflow()) {
            settings.upgrade = oldUpgrade;
            if (player != null)
                player.sendMessage(Text.translatable("extended_drawer.drawer.upgrade_fail"), true);
            return false;
        }

        ItemUtils.offerOrDrop(world, pos, side, player, new ItemStack(oldUpgrade));
        dumpExcess(world, pos, side, player);
        return true;
    }

    @Override
    public void dumpExcess(World world, BlockPos pos, @Nullable Direction side, @Nullable PlayerEntity player) {
        if (amount > getCapacity()) {
            var slots = getSlots();
            // Iterate slots in reverse order
            for (int i = slots.length - 1; i >= 0; i--) {
                var slot = slots[i];
                if (slot.isBlocked()) continue;

                var toDrop = slot.getAmount() - slot.getCapacity();
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
        var config = ExtendedDrawersConfig.get().storage();
        long capacity = config.compactingCapacity();
        if (config.stackSizeAffectsCapacity())
            capacity /= 64.0 / item.getItem().getMaxCount();
        if (settings.upgrade != null)
            capacity = settings.upgrade.modifier.applyAsLong(capacity);
        capacity *= getTotalCompression();
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
        long amount = 0;

        for (var slot : getSlots()) {
            amount += slot.insert(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return settings.voiding ? maxAmount : amount;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for (var slot : getSlots()) {
            amount += slot.extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
        return new StorageIterator();
    }

    public Slot getSlot(int index) {
        return getSlots()[index];
    }

    @Override
    public void setLocked(boolean locked) {
        if (!locked && amount == 0) {
            clear();
        }
        DrawerStorage.super.setLocked(locked);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        DrawerStorage.super.readNbt(nbt);
        item = ItemVariant.fromNbt(nbt.getCompound("item"));
        amount = nbt.getLong("amount");
        updatePending = true;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        DrawerStorage.super.writeNbt(nbt);
        nbt.put("item", item.toNbt());
        nbt.putLong("amount", amount);
    }

    /**
     * Returns all non-blocked slots in a new array
     */
    public Slot[] getActiveSlots() {
        int count = getActiveSlotCount();
        var result = new Slot[count];
        System.arraycopy(getSlots(), 0, result, 0, count);
        return result;
    }

    public int getActiveSlotCount() {
        var slots = getSlots();
        int size;
        for (size = 0; size < slots.length; size++) {
            if (slots[size].blocked) break;
        }
        return size;
    }

    public Slot[] getSlots() {
        if (updatePending) updateSlots();
        return slots;
    }

    private int getTotalCompression() {
        var slots = getActiveSlots();
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

    private void updateSlots() {
        for (var slot : slots) { // Disable all slots
            slot.reset(true);
        }

        var ladder = owner.getWorld() == null
                ? new CompressionLadder(List.of(new CompressionLadder.Step(item, 1)))
                : CompressionRecipeManager.of(owner.getWorld().getRecipeManager()).getLadder(item, owner.getWorld());
        var ladderSize = ladder.steps().size();
        var initialPosition = ladder.getPosition(item);
        if (initialPosition == -1) throw new IllegalStateException("Item is not on it's own recipe ladder. Did we lookup mid-reload?");

        int[] positions = chooseLadderPositions(ladderSize, initialPosition);
        for (int i = 0; i < positions.length; i++) {
            var position = positions[i];
            var step = ladder.steps().get(position);
            slots[i].compression = step.size();
            slots[i].item = step.item();
            slots[i].blocked = false;
        }
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
        } else if (size == 2) {
            return new int[]{ 0, 1 };
        }

        if (start == 0) { // Our item is at the bottom of the ladder: take as many entries above as possible
            return new int[]{ 0, 1, 2 };
        } else if (start == size - 1) { // Our item is at the top of the ladder: take as many entries down as possible, ensure lower ones come first
            return new int[]{start - 2, start - 1, start};
        } else { // We are in the middle of the ladder: pick one from both sides
            return new int[]{ start - 1, start, start + 1 };
        }
    }

    @Override
    protected void onFinalCommit() {
        update();
    }

    public long getAmount() {
        return amount;
    }

    record Snapshot(ItemVariant item, long amount) {
    }

    private class StorageIterator implements Iterator<StorageView<ItemVariant>> {
        private int index = slots.length - 1;

        @Override
        public boolean hasNext() {
            return index >= 0;
        }

        @Override
        public Slot next() {
            return getSlots()[index--];
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
            StoragePreconditions.notBlankNotNegative(item, maxAmount);
            if (blocked) return 0;
            if (!this.item.equals(item) && !this.item.isBlank()) return 0;
            if (!ExtendedDrawersConfig.get().misc().allowRecursion() && !item.getItem().canBeNested()) return 0;
            if (this.item.isBlank() && settings.locked && !settings.lockOverridden) return 0;

            if (this.item.isBlank()) { // Special case for when drawer doesn't have item
                updateSnapshots(transaction);
                CompactingDrawerStorage.this.item = item;
                updateSlots();
                // Insert into correct slot
                return CompactingDrawerStorage.this.insert(item, maxAmount, transaction);
            }

            var inserted = Math.min(maxAmount, getCapacity() - getAmount());
            if (inserted > 0) {
                updateSnapshots(transaction);
                amount += inserted * compression;
            }

            return inserted;
        }

        @Override
        public long extract(ItemVariant item, long maxAmount, TransactionContext transaction) {
            if (blocked) return 0;
            if (!this.item.equals(item)) return 0;

            var extracted = Math.min(maxAmount, getAmount());
            if (extracted > 0) {
                updateSnapshots(transaction);
                amount -= extracted * compression;
            }

            if (amount == 0 && !settings.locked) {
                clear();
            }

            return extracted;
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
            return item.isBlank();
        }

        @Override
        public ItemVariant getResource() {
            return item;
        }

        @Override
        public long getAmount() {
            return CompactingDrawerStorage.this.amount / compression;
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
    }
}
