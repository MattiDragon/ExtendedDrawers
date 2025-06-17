package io.github.mattidragon.extendeddrawers.storage;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.component.DrawerSlotComponent;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public DrawerBlockEntity getOwner() {
        return owner;
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
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!resource.equals(item) && !item.isBlank()) return 0;
        if (!ExtendedDrawers.CONFIG.get().misc().allowRecursion() && !resource.getItem().canBeNested()) return 0;
        if (item.isBlank() && settings.locked && !settings.lockOverridden) return 0;

        var inserted = Math.min(getCapacity() - amount, maxAmount);
        if (inserted > 0) {
            updateSnapshots(transaction);
            amount += inserted;
            if (item.isBlank()) {
                item = resource;
                settings.sortingDirty = true;
            }
        } else if (inserted < 0) {
            ExtendedDrawers.LOGGER.warn("Somehow inserted negative amount of items ({}) into drawer, aborting. Arguments: item={} maxAmount={}. Status: item={} capacity={} amount={}", inserted, item, maxAmount, this.item, getCapacity(), amount);
            return 0;
        }
        // In voiding mode we return the max even if it doesn't fit. We just delete it this way
        return settings.voiding ? maxAmount : inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (!resource.equals(item)) return 0;

        var extracted = Math.min(amount, maxAmount);
        if (extracted > 0) {
            updateSnapshots(transaction);
            amount -= extracted;
            if (amount == 0 && !settings.locked && !settings.duping) {
                item = ItemVariant.blank();
                settings.sortingDirty = true;
            }
        } else if (extracted < 0) {
            ExtendedDrawers.LOGGER.warn("Somehow extract negative amount of items ({}) from drawer, aborting. Arguments: item={} maxAmount={}. Status: item={} capacity={} amount={}", extracted, item, maxAmount, this.item, getCapacity(), amount);
            return 0;
        }
        return settings.duping ? maxAmount : extracted;
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
    public long getTrueAmount() {
        return amount;
    }

    @Override
    public long getAmount() {
        return settings.duping ? Long.MAX_VALUE : amount;
    }

    @Override
    public long getCapacity() {
        var config = ExtendedDrawers.CONFIG.get().storage();
        var capacity = (long) (config.drawerCapacity() * this.capacityMultiplier);
        if (config.stackSizeAffectsCapacity())
            capacity = (long) (capacity / (64.0 / item.getItem().getMaxCount()));
        if (getUpgrade() != null)
            capacity = getUpgrade().modifier.applyAsLong(capacity);
        capacity = Math.min(capacity, getLimiter());
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

    @Override
    public void dumpExcess(World world, BlockPos pos, @Nullable Direction side, @Nullable PlayerEntity player) {
        if (amount > getCapacity()) {
            ItemUtils.offerOrDropStacks(world, pos, side, player, item, amount - getCapacity());
            amount = getCapacity();
        }
        update();
    }

    @Override
    public void readData(ReadView view) {
        DrawerStorage.super.readData(view);
        item = view.read("item", ItemVariant.CODEC).orElseGet(ItemVariant::blank);
        amount = view.getLong("amount", 0);
        if (item.isBlank()) amount = 0; // Avoids dupes with drawers of removed items
    }

    @Override
    public void writeData(WriteView view) {
        DrawerStorage.super.writeData(view);
        view.put("item", ItemVariant.CODEC, item);
        view.putLong("amount", amount);
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

    protected record Snapshot(ResourceAmount<ItemVariant> contents, boolean itemChanged) {}
}
