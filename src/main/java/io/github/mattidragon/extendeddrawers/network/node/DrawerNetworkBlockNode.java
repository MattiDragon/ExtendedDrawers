package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeContext;
import com.kneelawk.graphlib.api.graph.NodeEntityContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.NodeEntity;
import com.kneelawk.graphlib.api.util.HalfLink;
import com.kneelawk.graphlib.api.wire.FullWireBlockNode;
import com.kneelawk.graphlib.api.wire.WireConnectionDiscoverers;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface DrawerNetworkBlockNode extends FullWireBlockNode {
    @Override
    @NotNull
    default Collection<HalfLink> findConnections(@NotNull NodeContext ctx) {
        return WireConnectionDiscoverers.fullBlockFindConnections(this, ctx, NetworkRegistry.CONNECTION_FILTER);
    }

    @Override
    default boolean canConnect(@NotNull NodeContext ctx, @NotNull HalfLink other) {
        return WireConnectionDiscoverers.fullBlockCanConnect(this, ctx, other, NetworkRegistry.CONNECTION_FILTER);
    }

    @Override
    default void onConnectionsChanged(@NotNull NodeContext ctx) {
        var graph = ctx.graphWorld().getGraph(ctx.self().getGraphId());
        if (graph != null) {
            graph.getGraphEntity(NetworkRegistry.UPDATE_HANDLER_TYPE).scheduleUpdate(UpdateHandler.ChangeType.CONTENT);
        }
    }

    @Override
    default boolean shouldHaveNodeEntity(@NotNull NodeContext ctx) {
        return FullWireBlockNode.super.shouldHaveNodeEntity(ctx);
    }

    @Override
    default @Nullable NodeEntity createNodeEntity(@NotNull NodeEntityContext entityCtx) {
        return FullWireBlockNode.super.createNodeEntity(entityCtx);
    }

    @Override
    @Nullable
    default NbtElement toTag() {
        return null;
    }

    void update(ServerWorld world, NodeHolder<BlockNode> node);
}
