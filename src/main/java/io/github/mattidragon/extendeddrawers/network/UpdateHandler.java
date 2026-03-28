package io.github.mattidragon.extendeddrawers.network;

import com.kneelawk.graphlib.api.graph.GraphEntityContext;
import com.kneelawk.graphlib.api.graph.user.GraphEntity;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import io.github.mattidragon.extendeddrawers.network.cache.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.apache.commons.lang3.ObjectUtils;
import org.jspecify.annotations.Nullable;

public class UpdateHandler implements GraphEntity<UpdateHandler> {
    @SuppressWarnings("NotNullFieldNotInitialized")
    private GraphEntityContext context;
    @Nullable
    private ChangeType queuedUpdate;

    public static void scheduleUpdate(ServerLevel level, BlockPos pos, ChangeType type) {
        NetworkRegistry.UNIVERSE.getGraphWorld(level)
                .getLoadedGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.UPDATE_HANDLER_TYPE))
                .forEach(updateHandler -> updateHandler.scheduleUpdate(type));
    }

    public void scheduleUpdate(ChangeType type) {
        queuedUpdate = ObjectUtils.max(queuedUpdate, type);
    }

    @Override
    public void onTick() {
        if (queuedUpdate == null) return;
        if (!(context.getBlockWorld() instanceof ServerLevel level)) return;

        NetworkStorageCache networkStorageCache = context.getGraph().getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE);
        // Structure updates are handled elsewhere and count updates don't matter
        if (queuedUpdate == ChangeType.CONTENT) {
            networkStorageCache.onSortingChanged();
        }

        context.getGraph()
                .getNodes()
                .forEach(node -> {
                    if (node.getNode() instanceof DrawerNetworkBlockNode drawerNode) {
                        drawerNode.update(level, node);
                    }
                });
        queuedUpdate = null;
    }

    @Override
    public void onInit(GraphEntityContext context) {
        this.context = context;
    }

    @Override
    public GraphEntityContext getContext() {
        return context;
    }

    @Override
    public GraphEntityType<?> getType() {
        return NetworkRegistry.UPDATE_HANDLER_TYPE;
    }

    @Override
    public void onUpdate() {
        scheduleUpdate(ChangeType.STRUCTURE);
    }

    @Override
    public void merge(UpdateHandler other) {
    }

    public enum ChangeType {
        COUNT, // Need to send packets to client
        CONTENT, // Need to sort slots (includes lock changes)
        STRUCTURE // Need to rebuild storage list
    }
}
