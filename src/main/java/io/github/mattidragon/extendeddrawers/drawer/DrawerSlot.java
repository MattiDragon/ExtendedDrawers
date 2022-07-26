package io.github.mattidragon.extendeddrawers.drawer;

import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public final class DrawerSlot extends SnapshotParticipant<DrawerSlot.Snapshot> implements SingleSlotStorage<ItemVariant>, Comparable<DrawerSlot> {
    // Fields are encapsulated to ensure updates on change
    private final BooleanConsumer onChange;
    /**
     * This flag dictates if the slot order of a network needs to be recalculated during the next update.
     */
    private boolean sortingChanged;
    /**
     * A multiplier for capacity set by the containing drawer. Depends on slot count.
     */
    private final double capacityMultiplier;
    /**
     * The item the slot contains, blank for empty.
     */
    private ItemVariant item = ItemVariant.blank();
    /**
     * The amount currently contained in the dlot.
     */
    private long amount;
    /**
     * An upgrade item that is installed. {@code null} for empty.
     */
    @Nullable
    private UpgradeItem upgrade = null;
    /**
     * Whether the slot is locked. The item in a locked slot doesn't change when empty.
     */
    private boolean locked;
    /**
     * Whether the slot is in voiding mode. Voiding slots delete overflowing items
     */
    private boolean voiding;

    public DrawerSlot(BooleanConsumer onChange, double capacityMultiplier) {
        this.onChange = onChange;
        this.capacityMultiplier = capacityMultiplier;
    }

    public void setVoiding(boolean voiding) {
        this.voiding = voiding;
        sortingChanged = true;
        update();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        sortingChanged = true;
        if (!locked && amount == 0) item = ItemVariant.blank();
        update();
    }

    /**
     * Attempts to change the upgrade of this slot.
     * @return Whether the change was successful.
     */
    public boolean changeUpgrade(@Nullable UpgradeItem newUpgrade, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player) {
        var oldUpgrade = upgrade;
        upgrade = newUpgrade;
        if (getCapacity() < getAmount() && CommonConfig.HANDLE.get().blockUpgradeRemovalsWithOverflow()) {
            upgrade = oldUpgrade;
            if (player != null)
                player.sendMessage(new TranslatableText("extended_drawer.drawer.upgrade_fail"), true);
            return false;
        }

        ItemUtils.offerOrDrop(world, pos, side, player, new ItemStack(oldUpgrade));
        dumpExcess(world, pos, side, player);
        return true;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!resource.equals(item) && !item.isBlank()) return 0;

        updateSnapshots(transaction);
        var inserted = Math.min(getCapacity() - amount, maxAmount);
        amount += inserted;
        if (item.isBlank()) {
            item = resource;
            sortingChanged = true;
        }
        // In voiding mode we return the max even if it doesn't fit. We just delete it this way
        return voiding ? maxAmount : inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!resource.equals(item)) return 0;

        updateSnapshots(transaction);
        var extracted = Math.min(amount, maxAmount);
        amount -= extracted;
        if (amount == 0 && !locked) {
            item = ItemVariant.blank();
            sortingChanged = true;
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
        if (upgrade != null)
            capacity = upgrade.modifier.applyAsLong(capacity);
        return capacity;
    }

    @Override
    protected Snapshot createSnapshot() {
        return new Snapshot(new ResourceAmount<>(item, amount), sortingChanged);
    }

    @Override
    protected void readSnapshot(Snapshot snapshot) {
        item = snapshot.contents.resource();
        amount = snapshot.contents.amount();
        sortingChanged = snapshot.itemChanged;
    }

    @Override
    protected void onFinalCommit() {
        update();
    }

    public void update() {
        onChange.accept(sortingChanged);
        sortingChanged = false;
    }

    public void dumpExcess(World world, BlockPos pos, Direction side, @Nullable PlayerEntity player) {
        if (amount > getCapacity()) {
            ItemUtils.offerOrDropStacks(world, pos, side, player, item, amount - getCapacity());
            amount = getCapacity();
        }
        update();
    }

    @Override
    public int compareTo(@NotNull DrawerSlot other) {
        if (this.isResourceBlank() != other.isResourceBlank())
            return this.isResourceBlank() ? 1 : -1;
        if (this.locked != other.locked)
            return this.locked ? -1 : 1;
        if (this.voiding != other.voiding)
            return this.voiding ? 1 : -1;

        return 0;
    }

    public void readNbt(NbtCompound nbt) {
        item = ItemVariant.fromNbt(nbt.getCompound("item"));
        amount = nbt.getLong("amount");
        locked = nbt.getBoolean("locked");
        voiding = nbt.getBoolean("voiding");
        upgrade = Registry.ITEM.get(Identifier.tryParse(nbt.getString("upgrade"))) instanceof UpgradeItem upgrade ? upgrade : null;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.put("item", item.toNbt());
        nbt.putLong("amount", amount);
        nbt.putBoolean("locked", locked);
        nbt.putBoolean("voiding", voiding);
        nbt.putString("upgrade", Registry.ITEM.getId(upgrade).toString());
    }

    public ItemVariant getItem() {
        return item;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isVoiding() {
        return voiding;
    }

    public @Nullable UpgradeItem getUpgrade() {
        return upgrade;
    }

    record Snapshot(ResourceAmount<ItemVariant> contents, boolean itemChanged) {}
}
