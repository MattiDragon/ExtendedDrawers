package io.github.mattidragon.extendeddrawers.network.node;

import com.kneelawk.graphlib.graph.BlockNodeHolder;
import com.kneelawk.graphlib.graph.struct.Node;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class AccessPointBlockNode implements DrawerNetworkBlockNode {
    public static final Identifier ID = id("access_point");
    public static final AccessPointBlockNode INSTANCE = new AccessPointBlockNode();

    private AccessPointBlockNode() {
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
    public void update(ServerWorld world, Node<BlockNodeHolder> node) {
    }
}
