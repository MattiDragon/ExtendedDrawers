package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.network.cache.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ShadowDrawerBlockEntity extends BlockEntity {
    public static final long INFINITE_COUNT_MARKER = -2;
    public ItemVariant item = ItemVariant.blank();
    /**
     * Stores the amount of items currently available. On the server this is a cache and on the client it stores the number synced from the server.
     */
    public long countCache = -1;
    private boolean hidden = false;
    
    public ShadowDrawerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY, pos, state);
    }
    
    static {
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.world instanceof ServerWorld serverWorld ? createStorage(serverWorld, drawer.pos) : Storage.empty(), ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY);
    }
    
    private static Storage<ItemVariant> createStorage(ServerWorld world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof ShadowDrawerBlockEntity shadowDrawer)) throw new IllegalStateException();
        
        return shadowDrawer.new ShadowDrawerStorage(NetworkStorageCache.get(world, pos));
    }
    
    public void recalculateContents() {
        if (world == null) return;

        if (this.world instanceof ServerWorld world && !item.isBlank()) {
            var storage = NetworkStorageCache.get(world, pos);
            long amount = 0L;
            outer:
            for (var slot : storage.parts) {
                for (var view : slot) {
                    if (slot.isDuping()) {
                        amount = INFINITE_COUNT_MARKER;
                        break outer;
                    }
                    if (view.getResource().equals(item)) {
                        amount += view.getAmount();
                    }
                }
            }
            countCache = amount;
        }
        var state = getCachedState();
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        world.updateComparators(pos, state.getBlock());
    }
    
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = new NbtCompound();
        writeNbt(nbt, registryLookup);
        nbt.putLong("count", countCache);
        nbt.putBoolean("hidden", isHidden());
        return nbt;
    }
    
    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (nbt.contains("count")) countCache = nbt.getLong("count");
        item = ItemVariant.CODEC.parse(RegistryOps.of(NbtOps.INSTANCE, registryLookup), nbt.getCompound("item")).getOrThrow();
        hidden = nbt.getBoolean("hidden");
    }
    
    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.put("item", ItemVariant.CODEC.encodeStart(RegistryOps.of(NbtOps.INSTANCE, registryLookup), item).getOrThrow());
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        var state = getCachedState();
        if (world != null) {
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    public class ShadowDrawerStorage extends FilteringStorage<ItemVariant> {
        public ShadowDrawerStorage(Storage<ItemVariant> backingStorage) {
            super(backingStorage);
        }
    
        @Override
        protected boolean canInsert(ItemVariant resource) {
            return resource.isBlank() || resource.equals(item);
        }
    
        @Override
        protected boolean canExtract(ItemVariant resource) {
            return resource.equals(item);
        }
    }
}
