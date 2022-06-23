package io.github.mattidragon.extendeddrawers.network;

import com.kneelawk.graphlib.graph.BlockNode;
import com.kneelawk.graphlib.graph.BlockNodeDiscoverer;
import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class DrawerNodeDiscoverer implements BlockNodeDiscoverer {
    @Override
    public @NotNull Collection<BlockNode> getNodesInBlock(@NotNull ServerWorld world, @NotNull BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof NetworkComponent component) {
            return component.createNodes();
        }
        
        return List.of();
    }
}
