package io.github.mattidragon.extendeddrawers.network.cache;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.GraphEntityContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.LinkEntity;
import com.kneelawk.graphlib.api.graph.user.NodeEntity;
import com.kneelawk.graphlib.api.util.LinkPos;
import io.github.mattidragon.extendeddrawers.block.entity.StorageProvidingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.network.node.CompactingDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerBlockNode;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * Caches storages of all slots in networks to make lookup less expensive.
 */
public class SmartNetworkStorageCache implements NetworkStorageCache {
    @SuppressWarnings("NotNullFieldNotInitialized")
    private GraphEntityContext context;
    private final CombinedStorage<ItemVariant, DrawerStorage> cachedStorage = new CombinedStorage<>(new ArrayList<>());
    private final Multimap<BlockPos, DrawerStorage> positions = HashMultimap.create();
    private final Set<BlockPos> missingPositions = new HashSet<>();

    @Override
    public CombinedStorage<ItemVariant, DrawerStorage> get() {
        update();
        return cachedStorage;
    }

    @Override
    public void update() {
        if (!missingPositions.isEmpty()) {
            missingPositions.forEach(pos -> {
                if (context.getBlockWorld().getBlockEntity(pos) instanceof StorageProvidingDrawerBlockEntity drawer) {
                    drawer.streamStorages().forEach(storage -> {
                        cachedStorage.parts.add(storage);
                        positions.put(pos, storage);
                    });
                }
            });
            missingPositions.clear();
            onSortingChanged();
        }
    }

    @Override
    public void onSortingChanged() {
        cachedStorage.parts.sort(null);
    }

    @Override
    public void onInit(GraphEntityContext context) {
        this.context = context;
        missingPositions.clear();
        context.getGraph().getNodes().map(NodeHolder::getBlockPos).filter(Predicate.not(positions::containsKey)).forEach(missingPositions::add);
    }

    @Override
    public GraphEntityContext getContext() {
        return context;
    }

    @Override
    public void onPostNodeCreated(NodeHolder<BlockNode> node, @Nullable NodeEntity nodeEntity) {
        missingPositions.add(node.getBlockPos());
    }

    @Override
    public void onPostNodeDestroyed(NodeHolder<BlockNode> node, @Nullable NodeEntity nodeEntity, Map<LinkPos, LinkEntity> linkEntities) {
        var pos = node.getBlockPos();

        // Remove storages from cache
        positions.get(pos).forEach(cachedStorage.parts::remove);
        positions.removeAll(pos);
        missingPositions.remove(pos);
    }

    @Override
    public void onNodeUnloaded(BlockPos pos) {
        positions.get(pos).forEach(cachedStorage.parts::remove);
        positions.removeAll(pos);
        missingPositions.add(pos);
    }

    @Override
    public void onNodeReloaded(BlockPos pos) {
        missingPositions.add(pos);
    }

    @Override
    public void merge(NetworkStorageCache other) {
        if (!(other instanceof SmartNetworkStorageCache smart)) {
            forceUpdate();
        } else {
            this.positions.putAll(smart.positions);
            this.missingPositions.addAll(smart.missingPositions);
            this.cachedStorage.parts.addAll(smart.cachedStorage.parts);
            this.onSortingChanged();
        }
    }

    @Override
    public SmartNetworkStorageCache split(BlockGraph originalGraph, BlockGraph newGraph) {
        var newCache = new SmartNetworkStorageCache();

        // Split position based storage cache
        for (var iterator = positions.entries().iterator(); iterator.hasNext(); ) {
            var entry = iterator.next();
            var pos = entry.getKey();
            var storage = entry.getValue();

            if (newGraph.getNodesAt(pos).findAny().isPresent()) {
                iterator.remove();
                cachedStorage.parts.remove(storage);
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
        newCache.onSortingChanged();
        onSortingChanged();

        return newCache;
    }

    @Override
    public List<Component> getDebugInfo() {
        var list = new ArrayList<Component>();
        list.add(Component.literal("Smart Storage Cache Debug Info").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
        list.add(Component.literal("  %s uncached positions".formatted(missingPositions.size())));
        list.add(Component.literal("  %s cached positions".formatted(positions.size())));
        list.add(Component.literal("  %s storages".formatted(cachedStorage.parts.size())));
        list.add(Component.empty());

        context.getGraph()
                .getNodes()
                .filter(holder -> holder.getNode() instanceof DrawerBlockNode || holder.getNode() instanceof CompactingDrawerBlockNode)
                .map(NodeHolder::getBlockPos)
                .forEach(pos -> {
                    list.add(Component.literal("%s".formatted(pos.toShortString())).withStyle(ChatFormatting.YELLOW));
                    var isValid = false;
                    if (missingPositions.contains(pos)) {
                        list.add(Component.literal("  Not cached").withStyle(ChatFormatting.RED));
                        isValid = true;
                    }
                    if (positions.containsKey(pos)) {
                        list.add(Component.literal("  Cached: %s storage(s)".formatted(positions.get(pos).size())).withStyle(ChatFormatting.GREEN));
                        isValid = true;
                    }

                    if (!isValid) {
                        list.add(Component.literal("  Missing from cache").withStyle(ChatFormatting.DARK_RED));
                    }
                });

        return list;
    }

    @Override
    public void forceUpdate() {
        cachedStorage.parts.clear();
        positions.clear();
        missingPositions.clear();
        context.getGraph()
                .getNodes()
                .map(NodeHolder::getBlockPos)
                .forEach(missingPositions::add);
        update();
    }

    @Override
    public Component getDebugInfo(BlockPos pos) {
        if (missingPositions.contains(pos)) {
            return Component.literal("Not cached").withStyle(ChatFormatting.GREEN);
        }
        if (positions.containsKey(pos)) {
            return Component.literal("Cached: %s storage(s)".formatted(positions.get(pos).size())).withStyle(ChatFormatting.GREEN);
        }
        return Component.literal("Missing from cache").withStyle(ChatFormatting.RED);
    }
}

