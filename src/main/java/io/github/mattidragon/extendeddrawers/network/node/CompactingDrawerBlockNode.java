package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.node.BlockNode;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class CompactingDrawerBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("compacting_drawer");
    public static final CompactingDrawerBlockNode INSTANCE = new CompactingDrawerBlockNode();

    private CompactingDrawerBlockNode() {
    }

    @Override
    public @NotNull Identifier getTypeId() {
        return ID;
    }
    
    @Override
    public @Nullable NbtElement toTag() {
        return null;
    }
    
    @Override
    public void update(ServerWorld world, NodeHolder<BlockNode> node) {
        var pos = node.getPos();
        var state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }
}
