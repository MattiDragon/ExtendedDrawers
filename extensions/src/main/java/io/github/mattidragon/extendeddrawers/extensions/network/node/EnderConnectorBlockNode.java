package io.github.mattidragon.extendeddrawers.extensions.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.EnderConnectorBlockEntity;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class EnderConnectorBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("ender_connector");
    public static final EnderConnectorBlockNode INSTANCE = new EnderConnectorBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    @Override
    public BlockNodeType getType() {
        return TYPE;
    }

    @Override
    public void onConnectionsChanged(NodeHolder<BlockNode> self) {
        if (self.getBlockEntity() instanceof EnderConnectorBlockEntity entity) {
            entity.updateRayCache(self);
            entity.setChanged();
            var state = self.getBlockState();
            self.getBlockWorld().sendBlockUpdated(self.getBlockPos(), state, state, Block.UPDATE_ALL);
        }
    }
}
