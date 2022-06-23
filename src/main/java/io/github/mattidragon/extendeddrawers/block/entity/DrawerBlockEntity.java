package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.drawer.DrawerSlot;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class DrawerBlockEntity extends BlockEntity {
    public final int slots = ((DrawerBlock)this.getCachedState().getBlock()).slots;
    public final DrawerSlot[] storages = new DrawerSlot[((DrawerBlock)this.getCachedState().getBlock()).slots];
    public final Storage<ItemVariant> combinedStorage;
    
    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.combinedStorage, ModBlocks.DRAWER_BLOCK_ENTITY);
    }
    
    public DrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DRAWER_BLOCK_ENTITY, pos, state);
        for (int i = 0; i < storages.length; i++) storages[i] = new DrawerSlot(this::onSlotChanged, CommonConfig.HANDLE.get().slotCountAffectsCapacity() ? 1.0 / slots : 1);
        combinedStorage = new CombinedStorage<>(List.of(storages));
    }
    
    private void onSlotChanged(boolean itemChanged) {
        markDirty();
        assert world != null;
        if (world instanceof ServerWorld serverWorld) {
            UpdateHandler.scheduleUpdate(serverWorld, pos, itemChanged ? UpdateHandler.ChangeType.CONTENT : UpdateHandler.ChangeType.COUNT);
            var state = getCachedState();
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
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
        var list = nbt.getList("items", NbtElement.COMPOUND_TYPE).stream().map(NbtCompound.class::cast).toList();
        for (int i = 0; i < list.size(); i++) {
            var storageNbt = list.get(i);
            storages[i].item = ItemVariant.fromNbt(storageNbt.getCompound("item"));
            storages[i].amount = storageNbt.getLong("amount");
            storages[i].locked = storageNbt.getBoolean("locked");
            storages[i].upgrade = Registry.ITEM.get(Identifier.tryParse(storageNbt.getString("upgrade"))) instanceof UpgradeItem upgrade ? upgrade : null;
        }
    }
    
    @Override
    public void writeNbt(NbtCompound nbt) {
        var list = new NbtList();
        for (var storage : storages) {
            var storageNbt = new NbtCompound();
            storageNbt.put("item", storage.item.toNbt());
            storageNbt.putLong("amount", storage.amount);
            storageNbt.putBoolean("locked", storage.locked);
            storageNbt.putString("upgrade", Registry.ITEM.getId(storage.upgrade).toString());
            list.add(storageNbt);
        }
        nbt.put("items", list);
    }
}
