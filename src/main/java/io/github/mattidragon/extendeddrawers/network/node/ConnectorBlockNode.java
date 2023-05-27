package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ConnectorBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("connector");
    public static final ConnectorBlockNode INSTANCE = new ConnectorBlockNode();

    private ConnectorBlockNode() {
    }

    @Override
    public @NotNull Identifier getTypeId() {
        return ID;
    }

    @Override
    public void update(ServerWorld world, NodeHolder<BlockNode> node) {
    }
}
