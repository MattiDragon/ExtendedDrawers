package io.github.mattidragon.extendeddrawers.drawer;

import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public final class DrawerSlot extends SnapshotParticipant<ResourceAmount<ItemVariant>> implements SingleSlotStorage<ItemVariant>, Comparable<DrawerSlot> {
    private final Runnable onChange;
    private final double capacityMultiplier;
    public ItemVariant item = ItemVariant.blank();
    @Nullable
    public UpgradeItem upgrade = null;
    public long amount;
    public boolean locked;
    
    public DrawerSlot(Runnable onChange, double capacityMultiplier) {
        this.onChange = onChange;
        this.capacityMultiplier = capacityMultiplier;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
        update();
        if (!locked && amount == 0) item = ItemVariant.blank();
    }
    
    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource != item && !item.isBlank()) return 0;
        
        updateSnapshots(transaction);
        var inserted = Math.min(getCapacity() - amount, maxAmount);
        amount += inserted;
        if (item.isBlank()) item = resource;
        return inserted;
    }
    
    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource != item) return 0;
        
        updateSnapshots(transaction);
        var extracted = Math.min(amount, maxAmount);
        amount -= extracted;
        if (amount == 0 && !locked) item = ItemVariant.blank();
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
        var multiplier = upgrade == null ? 1 : upgrade.multiplier;
        if (multiplier == -1) return 64;
        var capacity = (long) (config.defaultCapacity() * multiplier * this.capacityMultiplier);
        if (config.stackSizeAffectsCapacity())
            capacity /= 64.0 / item.getItem().getMaxCount();
        return capacity;
    }
    
    @Override
    protected ResourceAmount<ItemVariant> createSnapshot() {
        return new ResourceAmount<>(item, amount);
    }
    
    @Override
    protected void readSnapshot(ResourceAmount<ItemVariant> snapshot) {
        item = snapshot.resource();
        amount = snapshot.amount();
    }
    
    @Override
    protected void onFinalCommit() {
        update();
    }
    
    public void update() {
        onChange.run();
        
    }
    
    @Override
    public int compareTo(@NotNull DrawerSlot other) {
        if (this.isResourceBlank() != other.isResourceBlank())
            return this.isResourceBlank() ? 1 : -1;
        if (this.locked != other.locked)
            return this.locked ? -1 : 1;
        
        return 0;
    }
}
