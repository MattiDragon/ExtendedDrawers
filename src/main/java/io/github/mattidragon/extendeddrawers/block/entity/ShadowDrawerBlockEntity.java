package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.network.cache.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
        ItemStorage.SIDED.registerForBlockEntity((drawer, dir) -> drawer.level instanceof ServerLevel serverWorld ? createStorage(serverWorld, drawer.worldPosition) : Storage.empty(), ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY);
    }
    
    private static Storage<ItemVariant> createStorage(ServerLevel world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof ShadowDrawerBlockEntity shadowDrawer)) throw new IllegalStateException();
        
        return shadowDrawer.new ShadowDrawerStorage(NetworkStorageCache.get(world, pos));
    }
    
    public void recalculateContents() {
        if (level == null) return;

        if (this.level instanceof ServerLevel world && !item.isBlank()) {
            var storage = NetworkStorageCache.get(world, worldPosition);
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
        var state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
        level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        try (var logging = new ProblemReporter.ScopedCollector(this.problemPath(), ExtendedDrawers.LOGGER)) {
            var view = TagValueOutput.createWithContext(logging, registries);
            saveAdditional(view);
            view.putLong("count", countCache);
            return view.buildResult();
        }
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        countCache = view.getLongOr("count", countCache);
        item = view.read("item", ItemVariant.CODEC).orElseGet(ItemVariant::blank);
        hidden = view.getBooleanOr("hidden", false);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        view.store("item", ItemVariant.CODEC, item);
        view.putBoolean("hidden", hidden);
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        var state = getBlockState();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
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
