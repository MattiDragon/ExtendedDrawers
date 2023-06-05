package io.github.mattidragon.extendeddrawers.network.node;

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
    default Collection<HalfLink> findConnections(@NotNull NodeHolder<BlockNode> self) {
        return WireConnectionDiscoverers.fullBlockFindConnections(this, self, NetworkRegistry.CONNECTION_FILTER);
    }

    @Override
    default boolean canConnect(@NotNull NodeHolder<BlockNode> self, @NotNull HalfLink other) {
        return WireConnectionDiscoverers.fullBlockCanConnect(this, self, other, NetworkRegistry.CONNECTION_FILTER);
    }

    @Override
    default void onConnectionsChanged(@NotNull NodeHolder<BlockNode> self) {
        var graph = self.getGraphWorld().getGraph(self.getGraphId());
        if (graph != null) {
            graph.getGraphEntity(NetworkRegistry.UPDATE_HANDLER_TYPE).scheduleUpdate(UpdateHandler.ChangeType.CONTENT);
        }
    }

    @Override
    default boolean shouldHaveNodeEntity(@NotNull NodeHolder<BlockNode> self) {
        return FullWireBlockNode.super.shouldHaveNodeEntity(self);
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
