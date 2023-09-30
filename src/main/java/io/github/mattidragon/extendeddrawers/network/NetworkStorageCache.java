package io.github.mattidragon.extendeddrawers.network;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.GraphEntityContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.*;
import com.kneelawk.graphlib.api.util.LinkPos;
import io.github.mattidragon.extendeddrawers.block.entity.StorageDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Caches storages of all slots in networks to make lookup less expensive.
 */
@SuppressWarnings("UnstableApiUsage")
public class NetworkStorageCache implements GraphEntity<NetworkStorageCache> {
    private GraphEntityContext context;
    private final CombinedStorage<ItemVariant, DrawerStorage> cachedStorage = new CombinedStorage<>(new ArrayList<>());
    private final Multimap<BlockPos, DrawerStorage> positions = HashMultimap.create();
    private final Set<BlockPos> missingPositions = new HashSet<>();

    /**
     * Helper to easily get the cached storage from a world and pos.
     */
    public static CombinedStorage<ItemVariant, DrawerStorage> get(ServerWorld world, BlockPos pos) {
        return NetworkRegistry.UNIVERSE.getServerGraphWorld(world)
                .getLoadedGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .map(NetworkStorageCache::get)
                .findFirst()
                .orElseGet(() -> new CombinedStorage<>(new ArrayList<>()));
    }

    public CombinedStorage<ItemVariant, DrawerStorage> get() {
        addMissingStorages();
        return cachedStorage;
    }

    private void addMissingStorages() {
        if (!missingPositions.isEmpty()) {
            missingPositions.forEach(pos -> {
                if (context.getBlockWorld().getBlockEntity(pos) instanceof StorageDrawerBlockEntity drawer) {
                    drawer.streamStorages().forEach(cachedStorage.parts::add);
                }
            });
            missingPositions.clear();
            sort();
        }
    }

    public void sort() {
        cachedStorage.parts.sort(null);
    }

    @Override
    public void onInit(@NotNull GraphEntityContext context) {
        this.context = context;
        missingPositions.clear();
        context.getGraph().getNodes().map(NodeHolder::getBlockPos).forEach(missingPositions::add);
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
    public void onNodeCreated(@NotNull NodeHolder<BlockNode> node, @Nullable NodeEntity nodeEntity) {
        GraphEntity.super.onNodeCreated(node, nodeEntity);
        missingPositions.add(node.getBlockPos());
    }

    @Override
    public void onNodeDestroyed(@NotNull NodeHolder<BlockNode> node, @Nullable NodeEntity nodeEntity, Map<LinkPos, LinkEntity> linkEntities) {
        GraphEntity.super.onNodeDestroyed(node, nodeEntity, linkEntities);
        var pos = node.getBlockPos();

        // Remove storages from cache
        positions.get(pos).forEach(cachedStorage.parts::remove);
        missingPositions.remove(pos);
    }

    public void onNodeUnloaded(BlockPos pos) {
        positions.get(pos).forEach(cachedStorage.parts::remove);
        missingPositions.add(pos);
    }

    public void onNodeReloaded(BlockPos pos) {
        missingPositions.add(pos);
    }

    @Override
    public void merge(@NotNull NetworkStorageCache other) {
        this.positions.putAll(other.positions);
        this.missingPositions.addAll(other.missingPositions);
        this.cachedStorage.parts.addAll(other.cachedStorage.parts);
        this.sort();
    }

    public @NotNull NetworkStorageCache split(@NotNull BlockGraph originalGraph, @NotNull BlockGraph newGraph) {
        var newCache = new NetworkStorageCache();

        // Split position based storage cache
        for (var iterator = positions.entries().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            var pos = entry.getKey();
            var storage = entry.getValue();

            if (newGraph.getNodesAt(pos).findAny().isPresent()) {
                iterator.remove();
                newCache.positions.put(pos, storage);
            }
        }

        // Split positions missing from position cache
        for (var iterator = missingPositions.iterator(); iterator.hasNext(); ) {
            var pos = iterator.next();
            if (newGraph.getNodesAt(pos).findAny().isPresent()) {
                iterator.remove();
                newCache.missingPositions.add(pos);
            }
        }

        // Update storage of new cache and sort the storage of this one for good measure.
        newCache.cachedStorage.parts.addAll(newCache.positions.values());
        newCache.sort();
        sort();

        return newCache;
    }
}

