package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.misc.NetworkHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ShadowDrawerBlockEntity extends BlockEntity {
    public ItemVariant item = ItemVariant.blank();
    private long countCache = -1;
    
    public ShadowDrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY, pos, state);
    }
    
    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.createStorage(), ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY);
    }
    
    public Storage<ItemVariant> createStorage() {
        return new CombinedStorage<>(NetworkHelper.getConnectedStorages(world, pos).stream().filter(slot -> slot.item.equals(item) || slot.item.isBlank()).toList());
    }
    
    public void clearCountCache() {
        countCache = -1;
    }
    
    public long getCount() {
        if (countCache == -1) countCache = createStorage().simulateExtract(item, Long.MAX_VALUE, null);
        return countCache;
    }
    
    @Nullable
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
        item = ItemVariant.fromNbt(nbt.getCompound("item"));
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("item", item.toNbt());
    }
}
