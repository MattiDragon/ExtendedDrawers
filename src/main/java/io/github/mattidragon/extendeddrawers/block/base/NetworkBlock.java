package io.github.mattidragon.extendeddrawers.block.base;

import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkBlock extends Block implements NetworkComponent {
    protected NetworkBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (world instanceof ServerWorld serverWorld && neighborState.getBlock() instanceof NetworkComponent) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }
    
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world instanceof ServerWorld serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        super.onStateReplaced(state, world, pos, moved);
        if (world instanceof ServerWorld serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
    }
}
