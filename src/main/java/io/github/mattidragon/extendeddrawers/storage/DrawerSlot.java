package io.github.mattidragon.extendeddrawers.storage;

import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
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
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public final class DrawerSlot extends SnapshotParticipant<DrawerSlot.Snapshot> implements SingleSlotStorage<ItemVariant>, DrawerStorage {
    private final DrawerBlockEntity owner;
    private final Settings settings;
    /**
     * A multiplier for capacity set by the containing drawer. Depends on slot count.
     */
    private final double capacityMultiplier;
    /**
     * The item the slot contains, blank for empty.
     */
    private ItemVariant item = ItemVariant.blank();
    /**
     * The amount currently contained in the slot.
     */
    private long amount;

    public DrawerSlot(DrawerBlockEntity owner, double capacityMultiplier) {
        this.owner = owner;
        this.capacityMultiplier = capacityMultiplier;
        settings = new Settings();
    }

    /**
     * Attempts to change the upgrade of this slot.
     * @return Whether the change was successful.
     */
    @Override
    public boolean changeUpgrade(@Nullable UpgradeItem newUpgrade, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player) {
        var oldUpgrade = settings.upgrade;
        settings.upgrade = newUpgrade;
        if (getAmount() > getCapacity() && CommonConfig.HANDLE.get().blockUpgradeRemovalsWithOverflow()) {
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
    public DrawerBlockEntity getOwner() {
        return owner;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!resource.equals(item) && !item.isBlank()) return 0;
        if (!CommonConfig.HANDLE.get().allowRecursion() && !resource.getItem().canBeNested()) return 0;
        if (item.isBlank() && settings.locked && !settings.lockOverridden) return 0;

        updateSnapshots(transaction);
        var inserted = Math.min(getCapacity() - amount, maxAmount);
        amount += inserted;
        if (item.isBlank()) {
            item = resource;
            settings.sortingDirty = true;
        }
        // In voiding mode we return the max even if it doesn't fit. We just delete it this way
        return settings.voiding ? maxAmount : inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!resource.equals(item)) return 0;

        updateSnapshots(transaction);
        var extracted = Math.min(amount, maxAmount);
        amount -= extracted;
        if (amount == 0 && !settings.locked) {
            item = ItemVariant.blank();
            settings.sortingDirty = true;
        }
        return extracted;
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
        return amount;
    }

    @Override
    public long getCapacity() {
        var config = CommonConfig.HANDLE.get();
        var capacity = (long) (config.defaultCapacity() * this.capacityMultiplier);
        if (config.stackSizeAffectsCapacity())
            capacity /= 64.0 / item.getItem().getMaxCount();
        if (settings.upgrade != null)
            capacity = settings.upgrade.modifier.applyAsLong(capacity);
        return capacity;
    }

    @Override
    protected Snapshot createSnapshot() {
        return new Snapshot(new ResourceAmount<>(item, amount), settings.sortingDirty);
    }

    @Override
    protected void readSnapshot(Snapshot snapshot) {
        item = snapshot.contents.resource();
        amount = snapshot.contents.amount();
        settings.sortingDirty = snapshot.itemChanged;
    }

    @Override
    protected void onFinalCommit() {
        update();
    }

    private void dumpExcess(World world, BlockPos pos, Direction side, @Nullable PlayerEntity player) {
        if (amount > getCapacity()) {
            ItemUtils.offerOrDropStacks(world, pos, side, player, item, amount - getCapacity());
            amount = getCapacity();
        }
        update();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        DrawerStorage.super.readNbt(nbt);
        item = ItemVariant.fromNbt(nbt.getCompound("item"));
        amount = nbt.getLong("amount");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        DrawerStorage.super.writeNbt(nbt);
        nbt.put("item", item.toNbt());
        nbt.putLong("amount", amount);
    }

    public ItemVariant getItem() {
        return item;
    }

    public void setLocked(boolean locked) {
        if (!locked && amount == 0) {
            item = ItemVariant.blank();
        }
        DrawerStorage.super.setLocked(locked);
    }

    @Override
    public Settings settings() {
        return settings;
    }

    @Override
    public boolean isBlank() {
        return isResourceBlank();
    }

    record Snapshot(ResourceAmount<ItemVariant> contents, boolean itemChanged) {}
}
