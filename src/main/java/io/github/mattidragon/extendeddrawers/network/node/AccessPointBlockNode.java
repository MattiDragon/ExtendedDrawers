package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeContext;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.node.BlockNode;
import com.kneelawk.graphlib.api.node.NodeKey;
import com.kneelawk.graphlib.api.util.SimpleNodeKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public record AccessPointBlockNode(NodeContext context) implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("access_point");

    @Override
    public @NotNull Identifier getTypeId() {
        return ID;
    }

    @Override
    public void update(ServerWorld world, NodeHolder<BlockNode> node) {
    }
}
