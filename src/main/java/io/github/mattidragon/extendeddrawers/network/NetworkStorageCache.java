package io.github.mattidragon.extendeddrawers.network;

import com.kneelawk.graphlib.GraphLib;
import com.kneelawk.graphlib.graph.BlockGraph;
import com.kneelawk.graphlib.graph.BlockNodeHolder;
import com.kneelawk.graphlib.graph.struct.Node;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.drawer.DrawerSlot;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.LongFunction;

/**
 * Caches storages of all slots in networks to make lookup less expensive.
 */
@SuppressWarnings("UnstableApiUsage")
public class NetworkStorageCache {
    private static final Map<RegistryKey<World>, Long2ObjectMap<CombinedStorage<ItemVariant, DrawerSlot>>> CACHE = new HashMap<>();
    
    public static void update(ServerWorld world, long id, UpdateHandler.ChangeType type) {
        var worldCache = CACHE.get(world.getRegistryKey());
        if (worldCache == null || !worldCache.containsKey(id)) return;
        switch (type) {
            case STRUCTURE -> worldCache.remove(id);
            case CONTENT -> worldCache.get(id).parts.sort(null);
            case COUNT -> {}
        }
    }
    
    public static CombinedStorage<ItemVariant, DrawerSlot> get(ServerWorld world, BlockPos pos) {
        var optionalId = GraphLib.getController(world).getGraphsAt(pos).findFirst();
        if (optionalId.isEmpty()) {
            ExtendedDrawers.LOGGER.warn("Missing graph at " + pos);
            
            return new CombinedStorage<>(List.of());
        }
        var id = optionalId.getAsLong();
        return CACHE.computeIfAbsent(world.getRegistryKey(), key -> new Long2ObjectOpenHashMap<>())
                .computeIfAbsent(id, (LongFunction<CombinedStorage<ItemVariant, DrawerSlot>>) id_ ->
                    new CombinedStorage<>(getDrawerSlots(world, pos)));
    }
    
    @NotNull
    private static ArrayList<DrawerSlot> getDrawerSlots(ServerWorld world, BlockPos pos) {
        return new ArrayList<>(
                GraphLib.getController(world).getGraphsAt(pos)
                        .mapToObj(GraphLib.getController(world)::getGraph)
                        .filter(Objects::nonNull)
                        .flatMap(BlockGraph::getNodes)
                        .map(Node::data)
                        .map(BlockNodeHolder::getPos)
                        .map(world::getBlockEntity)
                        .filter(DrawerBlockEntity.class::isInstance)
                        .map(DrawerBlockEntity.class::cast)
                        .flatMap(drawer -> Arrays.stream(drawer.storages))
                        .sorted()
                        .toList());
    }
}

