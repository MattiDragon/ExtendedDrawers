package io.github.mattidragon.extendeddrawers.extensions.network.node;

import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import net.minecraft.resources.Identifier;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class DrawerBarrelBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("drawer_barrel");
    public static final DrawerBarrelBlockNode INSTANCE = new DrawerBarrelBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    private DrawerBarrelBlockNode() {
    }

    @Override
    public BlockNodeType getType() {
        return TYPE;
    }
}
