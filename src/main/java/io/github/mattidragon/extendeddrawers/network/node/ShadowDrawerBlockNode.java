package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.node.BlockNode;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ShadowDrawerBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("shadow_drawers");
    public static final ShadowDrawerBlockNode INSTANCE = new ShadowDrawerBlockNode();

    private ShadowDrawerBlockNode() {
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

        if (world.getBlockEntity(pos) instanceof ShadowDrawerBlockEntity drawer)
            drawer.recalculateContents();
    }
}
