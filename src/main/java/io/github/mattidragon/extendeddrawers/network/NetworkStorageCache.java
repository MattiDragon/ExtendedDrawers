package io.github.mattidragon.extendeddrawers.network;

import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.NetByteBuf;
import com.kneelawk.graphlib.api.graph.GraphEntityContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.GraphEntity;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import io.github.mattidragon.extendeddrawers.block.entity.StorageDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Caches storages of all slots in networks to make lookup less expensive.
 */
@SuppressWarnings("UnstableApiUsage")
public class NetworkStorageCache implements GraphEntity<NetworkStorageCache> {
    private final GraphEntityContext context;
    @Nullable
    private CombinedStorage<ItemVariant, DrawerStorage> cachedStorage = null;

    public NetworkStorageCache(GraphEntityContext context) {
        this.context = context;
    }

    /**
     * Helper to easily get the cached storage from a world and pos.
     */
    public static CombinedStorage<ItemVariant, DrawerStorage> get(ServerWorld world, BlockPos pos) {
        return NetworkRegistry.UNIVERSE.getServerGraphWorld(world)
                .getLoadedGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .map(NetworkStorageCache::get)
                .findFirst()
                .orElseGet(() -> new CombinedStorage<>(List.of()));
    }

    @NotNull
    private List<DrawerStorage> getStorages() {
        return new ArrayList<>(
                context.getGraph()
                        .getNodes()
                        .map(NodeHolder::getBlockPos)
                        .map(context.getBlockWorld()::getBlockEntity)
                        .filter(StorageDrawerBlockEntity.class::isInstance)
                        .map(StorageDrawerBlockEntity.class::cast)
                        .flatMap(StorageDrawerBlockEntity::streamStorages)
                        .sorted()
                        .toList());
    }

    public void update(UpdateHandler.ChangeType changeType) {
        switch (changeType) {
            case STRUCTURE -> cachedStorage = null;
            case CONTENT -> {
                if (cachedStorage != null) {
                    cachedStorage.parts.sort(null);
                }
            }
            case COUNT -> {}
        }
    }

    public CombinedStorage<ItemVariant, DrawerStorage> get() {
        if (cachedStorage == null) {
            cachedStorage = new CombinedStorage<>(getStorages());
        }
        return cachedStorage;
    }

    @Override
    public @NotNull GraphEntityContext getContext() {
        return context;
    }

    @Override
    public @NotNull GraphEntityType<?> getType() {
        return NetworkRegistry.STORAGE_CACHE_TYPE;
    }

    @Override
    public @Nullable NbtElement toTag() {
        return null;
    }

    @Override
    public void merge(@NotNull NetworkStorageCache other) {
    }
}

