package io.github.mattidragon.extendeddrawers.network.cache;

import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.user.GraphEntity;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;

public interface NetworkStorageCache extends GraphEntity<NetworkStorageCache> {
    /**
     * Helper to easily get the cached storage from a world and pos.
     */
    static CombinedStorage<ItemVariant, DrawerStorage> get(ServerLevel world, BlockPos pos) {
        return NetworkRegistry.UNIVERSE.getGraphWorld(world)
                .getLoadedGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE))
                .map(NetworkStorageCache::get)
                .findFirst()
                .orElseGet(() -> new CombinedStorage<>(new ArrayList<>()));
    }

    CombinedStorage<ItemVariant, DrawerStorage> get();

    void update();

    void forceUpdate();

    void onSortingChanged();

    void onNodeUnloaded(BlockPos pos);

    void onNodeReloaded(BlockPos pos);

    NetworkStorageCache split(BlockGraph originalGraph, BlockGraph newGraph);

    List<Component> getDebugInfo();

    Component getDebugInfo(BlockPos pos);

    @Override
    default GraphEntityType<?> getType() {
        return NetworkRegistry.STORAGE_CACHE_TYPE;
    }
}
