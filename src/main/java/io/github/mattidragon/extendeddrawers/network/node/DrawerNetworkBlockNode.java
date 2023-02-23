package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.graph.BlockNodeHolder;
import com.kneelawk.graphlib.graph.NodeView;
import com.kneelawk.graphlib.graph.struct.Node;
import com.kneelawk.graphlib.wire.FullWireBlockNode;
import com.kneelawk.graphlib.wire.WireConnectionDiscoverers;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface DrawerNetworkBlockNode extends FullWireBlockNode {
    @Override
    default @NotNull Collection<Node<BlockNodeHolder>> findConnections(@NotNull ServerWorld world, @NotNull NodeView nodeView, @NotNull BlockPos pos, @NotNull Node<BlockNodeHolder> self) {
        return WireConnectionDiscoverers.fullBlockFindConnections(this, world, nodeView, pos, self, NetworkRegistry.CONNECTION_FILTER);
    }
    
    @Override
    default boolean canConnect(@NotNull ServerWorld world, @NotNull NodeView nodeView, @NotNull BlockPos pos, @NotNull Node<BlockNodeHolder> self, @NotNull Node<BlockNodeHolder> other) {
        return WireConnectionDiscoverers.fullBlockCanConnect(this, world, pos, self, other, NetworkRegistry.CONNECTION_FILTER);
    }
    
    @Override
    default void onConnectionsChanged(@NotNull ServerWorld world, @NotNull BlockPos pos, @NotNull Node<BlockNodeHolder> self) {
        UpdateHandler.scheduleUpdate(world, self.data().getGraphId(), UpdateHandler.ChangeType.STRUCTURE);
    }
    
    void update(ServerWorld world, Node<BlockNodeHolder> node, UpdateHandler.ChangeType type);
}
