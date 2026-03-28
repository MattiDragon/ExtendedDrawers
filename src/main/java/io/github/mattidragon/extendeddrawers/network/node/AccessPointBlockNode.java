package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class AccessPointBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("access_point");
    public static final AccessPointBlockNode INSTANCE = new AccessPointBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    private AccessPointBlockNode() {
    }

    @Override
    public BlockNodeType getType() {
        return TYPE;
    }

    @Override
    public void update(ServerLevel level, NodeHolder<BlockNode> node) {
        level.updateNeighbourForOutputSignal(node.getBlockPos(), ModBlocks.ACCESS_POINT);
    }
}
