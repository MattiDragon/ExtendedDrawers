package io.github.mattidragon.extendeddrawers.network.cache;

import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.GraphEntityContext;
import io.github.mattidragon.extendeddrawers.block.entity.StorageProvidingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NoOpNetworkStorageCache implements NetworkStorageCache {
    @SuppressWarnings("NotNullFieldNotInitialized")
    private GraphEntityContext context;

    @Override
    public CombinedStorage<ItemVariant, DrawerStorage> get() {
        return new CombinedStorage<>(context.getGraph()
                .getNodes()
                .<DrawerStorage>flatMap(node -> {
                    if (node.getBlockEntity() instanceof StorageProvidingDrawerBlockEntity drawer) {
                        return drawer.streamStorages();
                    }
                    return Stream.empty();
                })
                .sorted()
                .toList());
    }

    @Override
    public void update() {
    }

    @Override
    public void forceUpdate() {
    }

    @Override
    public void onSortingChanged() {
    }

    @Override
    public void onNodeUnloaded(BlockPos pos) {
    }

    @Override
    public void onNodeReloaded(BlockPos pos) {
    }

    @Override
    public NetworkStorageCache split(BlockGraph originalGraph, BlockGraph newGraph) {
        return new NoOpNetworkStorageCache();
    }

    @Override
    public List<Component> getDebugInfo() {
        var list = new ArrayList<Component>();
        list.add(Component.literal("No-Op Storage Cache Debug Info").withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW));
        return list;
    }

    @Override
    public Component getDebugInfo(BlockPos pos) {
        return Component.literal("-");
    }

    @Override
    public void onInit(GraphEntityContext ctx) {
        this.context = ctx;
    }

    @Override
    public GraphEntityContext getContext() {
        return context;
    }

    @Override
    public void merge(NetworkStorageCache other) {
    }
}
