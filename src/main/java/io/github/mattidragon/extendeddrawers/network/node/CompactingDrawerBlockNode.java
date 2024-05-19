package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class CompactingDrawerBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("compacting_drawer");
    public static final CompactingDrawerBlockNode INSTANCE = new CompactingDrawerBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    private CompactingDrawerBlockNode() {
    }

    @Override
    public @NotNull BlockNodeType getType() {
        return TYPE;
    }
}
