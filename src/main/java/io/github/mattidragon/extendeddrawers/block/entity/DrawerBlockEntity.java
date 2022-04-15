package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class DrawerBlockEntity extends BlockEntity {
    public final DrawerSlot[] storages = new DrawerSlot[((DrawerBlock)this.getCachedState().getBlock()).slots];
    public final Storage<ItemVariant> combinedStorage;
    
    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.combinedStorage, ModBlocks.DRAWER_BLOCK_ENTITY);
    }
    
    public DrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DRAWER_BLOCK_ENTITY, pos, state);
        for (int i = 0; i < storages.length; i++) storages[i] = new DrawerSlot();
        combinedStorage = new CombinedStorage<>(List.of(storages));
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        var nbt = new NbtCompound();
        writeNbt(nbt);
        return nbt;
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        
        var list = nbt.getList("items", NbtElement.COMPOUND_TYPE).stream().map(NbtCompound.class::cast).toList();
        for (int i = 0; i < list.size(); i++) {
            var storageNbt = list.get(i);
            storages[i].item = ItemVariant.fromNbt(storageNbt.getCompound("item"));
            storages[i].amount = storageNbt.getLong("amount");
            storages[i].locked = storageNbt.getBoolean("locked");
        }
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        
        var list = new NbtList();
        for (var storage : storages) {
            var storageNbt = new NbtCompound();
            storageNbt.put("item", storage.item.toNbt());
            storageNbt.putLong("amount", storage.amount);
            storageNbt.putBoolean("locked", storage.locked);
            list.add(storageNbt);
        }
        nbt.put("items", list);
    }
    
    public final class DrawerSlot extends SnapshotParticipant<ResourceAmount<ItemVariant>> implements SingleSlotStorage<ItemVariant>, Comparable<DrawerSlot> {
        public ItemVariant item = ItemVariant.blank();
        public long amount;
        public boolean locked;
        
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
            return 1024; // TODO: upgrades
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
            markDirty();
            assert world != null;
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
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
}
