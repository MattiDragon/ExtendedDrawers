package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.wire.FullWireBlockNode;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

public interface DrawerNetworkBlockNode extends FullWireBlockNode {
    @Override
    default void onConnectionsChanged(@NotNull NodeHolder<BlockNode> self) {
        var graph = self.getGraphWorld().getGraph(self.getGraphId());
        if (graph != null) {
            graph.getGraphEntity(NetworkRegistry.UPDATE_HANDLER_TYPE).scheduleUpdate(UpdateHandler.ChangeType.CONTENT);
        }
    }

    default void update(ServerWorld world, NodeHolder<BlockNode> node) {
    }
}
