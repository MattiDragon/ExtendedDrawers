package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.BlockNodeType;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ShadowDrawerBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("shadow_drawers");
    public static final ShadowDrawerBlockNode INSTANCE = new ShadowDrawerBlockNode();
    public static final BlockNodeType TYPE = BlockNodeType.of(ID, () -> INSTANCE);

    private ShadowDrawerBlockNode() {
    }

    @Override
    public BlockNodeType getType() {
        return TYPE;
    }

    @Override
    public void update(ServerLevel level, NodeHolder<BlockNode> node) {
        var pos = node.getBlockPos();

        if (level.getBlockEntity(pos) instanceof ShadowDrawerBlockEntity drawer)
            drawer.recalculateContents();
    }
}
