package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.graph.BlockNode;
import com.kneelawk.graphlib.graph.BlockNodeDecoder;
import com.kneelawk.graphlib.graph.BlockNodeHolder;
import com.kneelawk.graphlib.graph.struct.Node;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ShadowDrawerBlockNode extends AbstractDrawerBlockNode {
    public static final Identifier ID = id("shadow_drawers");
    public static final BlockNodeDecoder DECODER = new Decoder();
    
    @Override
    public @NotNull Identifier getTypeId() {
        return ID;
    }
    
    @Override
    public @Nullable NbtElement toTag() {
        return null;
    }
    
    @Override
    public void update(ServerWorld world, Node<BlockNodeHolder> node, UpdateHandler.ChangeType type) {
        var pos = node.data().getPos();
        var state = world.getBlockState(pos);
        
        if (world.getBlockEntity(pos) instanceof ShadowDrawerBlockEntity drawer)
            drawer.recalculateContents();
    }
    
    private static class Decoder implements BlockNodeDecoder {
        @Override
        public @Nullable BlockNode createBlockNodeFromTag(@Nullable NbtElement tag) {
            return new ShadowDrawerBlockNode();
        }
    }
}
