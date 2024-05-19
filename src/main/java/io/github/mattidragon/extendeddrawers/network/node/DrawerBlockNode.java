package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class DrawerBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("drawer");
    public static final DrawerBlockNode INSTANCE = new DrawerBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    private DrawerBlockNode() {
    }

    @Override
    public @NotNull BlockNodeType getType() {
        return TYPE;
    }
}
