package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class CompactingDrawerBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("compacting_drawer");
    public static final CompactingDrawerBlockNode INSTANCE = new CompactingDrawerBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, tag -> INSTANCE);

    private CompactingDrawerBlockNode() {
    }

    @Override
    public @NotNull BlockNodeType getType() {
        return TYPE;
    }

    @Override
    public void update(ServerWorld world, NodeHolder<BlockNode> node) {
        var pos = node.getBlockPos();
        var state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }
}
