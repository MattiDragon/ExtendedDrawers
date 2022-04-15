package io.github.mattidragon.extendeddrawers.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseBlock<T extends BlockEntity> extends BlockWithEntity {
    protected BaseBlock(Settings settings) {
        super(settings);
    }
    
    protected abstract BlockEntityType<T> getType();
    
    protected final T getBlockEntity(World world, BlockPos pos) {
        return getType().get(world, pos);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return getType().instantiate(pos, state);
    }
}
