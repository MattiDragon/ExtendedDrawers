package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import net.minecraft.resources.Identifier;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ConnectorBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("connector");
    public static final ConnectorBlockNode INSTANCE = new ConnectorBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    private ConnectorBlockNode() {
    }

    @Override
    public BlockNodeType getType() {
        return TYPE;
    }
}
