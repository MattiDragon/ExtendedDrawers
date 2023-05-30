package io.github.mattidragon.extendeddrawers.network;

import com.kneelawk.graphlib.api.graph.GraphEntityContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.GraphEntity;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import com.kneelawk.graphlib.api.graph.user.NodeEntity;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateHandler implements GraphEntity<UpdateHandler> {
    private final GraphEntityContext context;
    @Nullable
    private ChangeType queuedUpdate;

    public UpdateHandler(GraphEntityContext context) {
        this.context = context;
    }

    public static void scheduleUpdate(ServerWorld world, BlockPos pos, ChangeType type) {
        NetworkRegistry.UNIVERSE.getGraphWorld(world)
                .getLoadedGraphsAt(pos)
                .map(graph -> graph.getGraphEntity(NetworkRegistry.UPDATE_HANDLER_TYPE))
                .forEach(updateHandler -> updateHandler.scheduleUpdate(type));
    }

    public static void flushUpdates(ServerWorld world) {
        var graphWorld = NetworkRegistry.UNIVERSE.getGraphWorld(world);

        graphWorld.getLoadedGraphs()
                .map(graph -> graph.getGraphEntity(NetworkRegistry.UPDATE_HANDLER_TYPE))
                .forEach(UpdateHandler::apply);
    }

    public void scheduleUpdate(ChangeType type) {
        queuedUpdate = ObjectUtils.max(queuedUpdate, type);
    }

    private void apply() {
        if (queuedUpdate == null) return;

        context.getGraph().getGraphEntity(NetworkRegistry.STORAGE_CACHE_TYPE).update(queuedUpdate);

        context.getGraph()
                .getNodes()
                .forEach(node -> {
                    if (node.getNode() instanceof DrawerNetworkBlockNode drawerNode) {
                        drawerNode.update(context.getBlockWorld(), node);
                    }
                });
    }

    @Override
    public @NotNull GraphEntityType<?> getType() {
        return NetworkRegistry.UPDATE_HANDLER_TYPE;
    }

    @Override
    public @Nullable NbtElement toTag() {
        return null;
    }

    @Override
    public void onUpdate() {
        scheduleUpdate(ChangeType.STRUCTURE);
    }

    @Override
    public void merge(@NotNull UpdateHandler other) {
    }

    public enum ChangeType {
        COUNT, // Need to send packets to client
        CONTENT, // Need to sort slots (includes lock changes)
        STRUCTURE // Need to rebuild storage list
    }
}
