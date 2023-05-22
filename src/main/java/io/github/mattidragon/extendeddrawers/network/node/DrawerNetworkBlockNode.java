package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.node.*;
import com.kneelawk.graphlib.api.wire.FullWireBlockNode;
import com.kneelawk.graphlib.api.wire.WireConnectionDiscoverers;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface DrawerNetworkBlockNode extends FullWireBlockNode {
    NodeContext context();

    @Override
    @NotNull
    default Collection<NodeHolder<BlockNode>> findConnections() {
        return WireConnectionDiscoverers.fullBlockFindConnections(this, context(), NetworkRegistry.CONNECTION_FILTER);
    }

    @Override
    default boolean canConnect(@NotNull NodeHolder<BlockNode> other) {
        return WireConnectionDiscoverers.fullBlockCanConnect(this, context(), other, NetworkRegistry.CONNECTION_FILTER);
    }

    @Override
    default void onConnectionsChanged() {
        UpdateHandler.scheduleUpdate(context().getBlockWorld(), context().getGraphId(), UpdateHandler.ChangeType.STRUCTURE);
    }

    @Override
    @NotNull
    default NodeKey getKey() {
        return NetworkRegistry.KEY;
    }

    @Override
    @Nullable
    default NbtElement toTag() {
        return null;
    }

    @Override
    default void onUnload() {
        NetworkStorageCache.remove(context().getBlockWorld(), context().getGraphId());
    }

    void update(ServerWorld world, NodeHolder<BlockNode> node);
}
