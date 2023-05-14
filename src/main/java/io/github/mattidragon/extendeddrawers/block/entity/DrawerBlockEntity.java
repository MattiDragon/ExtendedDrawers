package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.drawer.DrawerSlot;
import io.github.mattidragon.extendeddrawers.drawer.DrawerStorage;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class DrawerBlockEntity extends StorageDrawerBlockEntity {
    public final int slots = ((DrawerBlock)this.getCachedState().getBlock()).slots;
    public final DrawerSlot[] storages = new DrawerSlot[((DrawerBlock)this.getCachedState().getBlock()).slots];
    public final CombinedStorage<ItemVariant, DrawerSlot> combinedStorage;
    
    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.combinedStorage, ModBlocks.DRAWER_BLOCK_ENTITY);
    }
    
    public DrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DRAWER_BLOCK_ENTITY, pos, state);
        var capacityMultiplier = CommonConfig.HANDLE.get().slotCountAffectsCapacity() ? 1.0 / slots : 1;
        for (int i = 0; i < storages.length; i++) {
            storages[i] = new DrawerSlot(this, capacityMultiplier);
        }
        combinedStorage = new CombinedStorage<>(new ArrayList<>(List.of(storages)));
        sortSlots();
    }

    private void sortSlots() {
        combinedStorage.parts.sort(null);
    }

    public void onSlotChanged(boolean sortingChanged) {
        if (sortingChanged) sortSlots();
        super.onSlotChanged(sortingChanged);
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
    public Stream<? extends DrawerStorage> streamStorages() {
        return Arrays.stream(storages);
    }

    @Override
    public boolean isEmpty() {
        for (var storage : storages) {
            if (storage.getUpgrade() != null || !storage.isResourceBlank() || storage.isHidden() || storage.isLocked() || storage.isVoiding())
                return false;
        }
        return true;
    }
    
    @Override
    public void readNbt(NbtCompound nbt) {
        var list = nbt.getList("items", NbtElement.COMPOUND_TYPE).stream().map(NbtCompound.class::cast).toList();
        for (int i = 0; i < list.size(); i++) {
            storages[i].readNbt(list.get(i));
        }
        sortSlots();
    }
    
    @Override
    public void writeNbt(NbtCompound nbt) {
        var list = new NbtList();
        for (var storage : storages) {
            var storageNbt = new NbtCompound();
            storage.writeNbt(storageNbt);
            list.add(storageNbt);
        }
        nbt.put("items", list);
    }
}
