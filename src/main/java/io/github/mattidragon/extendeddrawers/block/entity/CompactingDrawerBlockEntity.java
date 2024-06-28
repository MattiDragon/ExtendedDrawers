package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;

public class CompactingDrawerBlockEntity extends StorageDrawerBlockEntity {
    public final CompactingDrawerStorage storage;

    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.storage, ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY);
    }

    public CompactingDrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY, pos, state);

        storage = new CompactingDrawerStorage(this);
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
        var component = components.get(ModDataComponents.COMPACTING_DRAWER_CONTENTS);
        if (component != null) {
            storage.readComponent(component);
        }
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        componentMapBuilder.add(ModDataComponents.COMPACTING_DRAWER_CONTENTS, storage.toComponent());
    }

    @Override
    public Stream<? extends DrawerStorage> streamStorages() {
        return Stream.of(storage);
    }

    @Override
    public boolean isEmpty() {
        return storage.getUpgrade() == null && storage.isBlank() && !storage.isHidden() && !storage.isLocked() && !storage.isVoiding();
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        storage.readNbt(nbt.getCompound("storage"), registryLookup);
    }
    
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        var storageNbt = new NbtCompound();
        storage.writeNbt(storageNbt, registryLookup);
        nbt.put("storage", storageNbt);
    }
}
