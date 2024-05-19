package io.github.mattidragon.extendeddrawers.network.cache;

import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.GraphEntityContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.LinkEntity;
import com.kneelawk.graphlib.api.graph.user.NodeEntity;
import com.kneelawk.graphlib.api.util.LinkPos;
import io.github.mattidragon.extendeddrawers.block.entity.StorageDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple implementation of {@link NetworkStorageCache} that clears itself on any change.
 */
public class SimpleNetworkStorageCache implements NetworkStorageCache {
    private GraphEntityContext context;
    @Nullable
    private CombinedStorage<ItemVariant, DrawerStorage> cachedStorage = null;
    /**
     * Stores if an update is currently in progress.
     * This is required as the block entity query in the update can trigger a cancelRemoval call on the block entity which in turn results in a clear which breaks the update.
     * It should be safe to ignore the update as no significant changes should have happened.
     */
    private boolean updating = false;

    @Override
    public CombinedStorage<ItemVariant, DrawerStorage> get() {
        if (cachedStorage == null) update();
        return cachedStorage;
    }

    private void clear() {
        if (updating) {
            return;
        }
        cachedStorage = null;
    }

    @Override
    public void update() {
        try {
            updating = true;
            cachedStorage = new CombinedStorage<>(new ArrayList<>());
            context.getGraph()
                    .getNodes()
                    .forEach(node -> {
                        if (node.getBlockEntity() instanceof StorageDrawerBlockEntity drawer) {
                            drawer.streamStorages().forEach(storage -> cachedStorage.parts.add(storage));
                        }
                    });
            cachedStorage.parts.sort(null);
        } finally {
            updating = false;
        }
    }

    @Override
    public void forceUpdate() {
        update();
    }

    @Override
    public void onSortingChanged() {
        if (cachedStorage != null)
            cachedStorage.parts.sort(null);
    }

    @Override
    public void onPostNodeCreated(@NotNull NodeHolder<BlockNode> node, @Nullable NodeEntity nodeEntity) {
        clear();
    }

    @Override
    public void onPostNodeDestroyed(@NotNull NodeHolder<BlockNode> node, @Nullable NodeEntity nodeEntity, Map<LinkPos, LinkEntity> linkEntities) {
        clear();
    }

    @Override
    public void onNodeUnloaded(BlockPos pos) {
        clear();
    }

    @Override
    public void onNodeReloaded(BlockPos pos) {
        clear();
    }

    @Override
    public @NotNull NetworkStorageCache split(@NotNull BlockGraph originalGraph, @NotNull BlockGraph newGraph) {
        clear();
        return new SimpleNetworkStorageCache();
    }

    @Override
    public List<Text> getDebugInfo() {
        var list = new ArrayList<Text>();
        list.add(Text.literal("Simple Storage Cache Debug Info").formatted(Formatting.BOLD, Formatting.YELLOW));
        list.add(Text.literal("  Cached: %s".formatted(cachedStorage != null)));
        return list;
    }

    @Override
    public Text getDebugInfo(BlockPos pos) {
        return Text.literal("-");
    }

    @Override
    public void onInit(@NotNull GraphEntityContext ctx) {
        this.context = ctx;
    }

    @Override
    public @NotNull GraphEntityContext getContext() {
        return context;
    }

    @Override
    public void merge(@NotNull NetworkStorageCache other) {
        clear();
    }
}
