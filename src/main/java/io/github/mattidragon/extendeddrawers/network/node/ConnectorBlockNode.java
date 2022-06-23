package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.graph.BlockNode;
import com.kneelawk.graphlib.graph.BlockNodeDecoder;
import com.kneelawk.graphlib.graph.BlockNodeHolder;
import com.kneelawk.graphlib.graph.struct.Node;
import io.github.mattidragon.extendeddrawers.network.UpdateHandler;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ConnectorBlockNode extends AbstractDrawerBlockNode {
    public static final Identifier ID = id("connector");
    public static final BlockNodeDecoder DECODER = new Decoder();
    
    @Override
    public void update(ServerWorld world, Node<BlockNodeHolder> node, UpdateHandler.ChangeType type) {
    
    }
    
    @Override
    public @NotNull Identifier getTypeId() {
        return ID;
    }
    
    @Override
    public @Nullable NbtElement toTag() {
        return null;
    }
    
    private static class Decoder implements BlockNodeDecoder {
        @Override
        public @Nullable BlockNode createBlockNodeFromTag(@Nullable NbtElement tag) {
            return new DrawerBlockNode();
        }
    }
}
