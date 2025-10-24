package io.github.mattidragon.extendeddrawers.extensions.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.EnderConnectorBlockEntity;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class EnderConnectorBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("ender_connector");
    public static final EnderConnectorBlockNode INSTANCE = new EnderConnectorBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    @Override
    public @NotNull BlockNodeType getType() {
        return TYPE;
    }

    @Override
    public void onConnectionsChanged(@NotNull NodeHolder<BlockNode> self) {
        if (self.getBlockEntity() instanceof EnderConnectorBlockEntity entity) {
            entity.updateRayCache(self);
            entity.markDirty();
            var state = self.getBlockState();
            self.getBlockWorld().updateListeners(self.getBlockPos(), state, state, Block.NOTIFY_ALL);
        }
    }
}
