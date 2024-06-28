package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.component.DrawerContentsComponent;
import io.github.mattidragon.extendeddrawers.component.DrawerSlotComponent;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.storage.CombinedDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerSlot;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class DrawerBlockEntity extends StorageDrawerBlockEntity {
    public final int slots = ((DrawerBlock)this.getCachedState().getBlock()).slots;
    public final DrawerSlot[] storages = new DrawerSlot[((DrawerBlock)this.getCachedState().getBlock()).slots];
    public final CombinedDrawerStorage combinedStorage;
    
    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.combinedStorage, ModBlocks.DRAWER_BLOCK_ENTITY);
    }
    
    public DrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.DRAWER_BLOCK_ENTITY, pos, state);
        var capacityMultiplier = ExtendedDrawers.CONFIG.get().storage().slotCountAffectsCapacity() ? 1.0 / slots : 1;
        for (int i = 0; i < storages.length; i++) {
            storages[i] = new DrawerSlot(this, capacityMultiplier);
        }
        combinedStorage = new CombinedDrawerStorage(storages);
        sortSlots();
    }

    private void sortSlots() {
        combinedStorage.sort();
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
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = new NbtCompound();
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    @Override
    protected void readComponents(ComponentsAccess components) {
        var component = components.get(ModDataComponents.DRAWER_CONTENTS);
        if (component == null) return;
        for (int i = 0; i < component.slots().size(); i++) {
            storages[i].readComponent(component.slots().get(i));
        }
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        if (isEmpty()) return;
        var slotComponents = new ArrayList<DrawerSlotComponent>();
        for (var storage : storages) {
            slotComponents.add(storage.toComponent());
        }
        componentMapBuilder.add(ModDataComponents.DRAWER_CONTENTS, new DrawerContentsComponent(slotComponents));
    }

    @Override
    public Stream<? extends DrawerStorage> streamStorages() {
        return Arrays.stream(storages);
    }

    @Override
    public boolean isEmpty() {
        for (var storage : storages) {
            if (storage.getUpgrade() != null || storage.getLimiter() != Long.MAX_VALUE || !storage.isResourceBlank() || storage.isHidden() || storage.isLocked() || storage.isVoiding()|| storage.isDuping())
                return false;
        }
        return true;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        var list = nbt.getList("items", NbtElement.COMPOUND_TYPE).stream().map(NbtCompound.class::cast).toList();
        for (int i = 0; i < list.size(); i++) {
            storages[i].readNbt(list.get(i), registryLookup);
        }
        sortSlots();
    }
    
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        var list = new NbtList();
        for (var storage : storages) {
            var storageNbt = new NbtCompound();
            storage.writeNbt(storageNbt, registryLookup);
            list.add(storageNbt);
        }
        nbt.put("items", list);
    }
}
