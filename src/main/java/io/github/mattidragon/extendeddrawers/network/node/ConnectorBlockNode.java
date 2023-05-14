package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.graph.BlockNodeHolder;
import com.kneelawk.graphlib.graph.struct.Node;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ConnectorBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("connector");
    public static final ConnectorBlockNode INSTANCE = new ConnectorBlockNode();

    private ConnectorBlockNode() {
    }

    @Override
    public void update(ServerWorld world, Node<BlockNodeHolder> node) {
    }
    
    @Override
    public @NotNull Identifier getTypeId() {
        return ID;
    }
    
    @Override
    public @Nullable NbtElement toTag() {
        return null;
    }
}
