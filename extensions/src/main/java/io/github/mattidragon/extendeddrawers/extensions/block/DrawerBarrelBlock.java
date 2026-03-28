package io.github.mattidragon.extendeddrawers.extensions.block;

import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.DrawerBarrelBlockEntity;
import io.github.mattidragon.extendeddrawers.extensions.network.node.DrawerBarrelBlockNode;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class DrawerBarrelBlock extends BarrelBlock implements NetworkComponent {
    public DrawerBarrelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public DrawerNetworkBlockNode getNode() {
        return DrawerBarrelBlockNode.INSTANCE;
    }


    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (level instanceof ServerLevel serverWorld && neighbourState.getBlock() instanceof NetworkComponent) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        if (level instanceof ServerLevel serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
        if (level instanceof ServerLevel serverWorld) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld).updateNodes(pos);
        }
    }

    @Override
    protected void updateIndirectNeighbourShapes(BlockState state, LevelAccessor level, BlockPos pos, @UpdateFlags int updateFlags, int updateLimit) {
        super.updateIndirectNeighbourShapes(state, level, pos, updateFlags, updateLimit);
        if (level instanceof ServerLevel serverLevel) {
            NetworkRegistry.UNIVERSE.getGraphWorld(serverLevel).updateNodes(pos);
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new DrawerBarrelBlockEntity(worldPosition, blockState);
    }
}
