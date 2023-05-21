package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.GraphView;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.node.BlockNode;
import com.kneelawk.graphlib.api.node.NodeKeyExtra;
import com.kneelawk.graphlib.api.wire.FullWireBlockNode;
import com.kneelawk.graphlib.api.wire.WireConnectionDiscoverers;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface DrawerNetworkBlockNode extends FullWireBlockNode, NodeKeyExtra {
    @Override
    @NotNull
    default NodeKeyExtra getKeyExtra() {
        return this;
    }

    @Override
    default boolean canConnect(@NotNull NodeHolder<BlockNode> self, @NotNull ServerWorld world, @NotNull GraphView graphView, @NotNull NodeHolder<BlockNode> other) {
        return WireConnectionDiscoverers.fullBlockCanConnect(this, self, world, other, NetworkRegistry.CONNECTION_FILTER);
    }

    @Override
    @NotNull
    default Collection<NodeHolder<BlockNode>> findConnections(@NotNull NodeHolder<BlockNode> self, @NotNull ServerWorld world, @NotNull GraphView graphView) {
        return WireConnectionDiscoverers.fullBlockFindConnections(this, self, world, graphView, NetworkRegistry.CONNECTION_FILTER);
    }

    @Override
    default void onConnectionsChanged(@NotNull NodeHolder<BlockNode> self, @NotNull ServerWorld world, @NotNull GraphView graphView) {
        UpdateHandler.scheduleUpdate(world, self.getGraphId(), UpdateHandler.ChangeType.STRUCTURE);
    }
    
    void update(ServerWorld world, NodeHolder<BlockNode> node);
}
