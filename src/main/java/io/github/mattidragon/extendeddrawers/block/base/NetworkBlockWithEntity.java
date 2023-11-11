package io.github.mattidragon.extendeddrawers.block.base;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkBlockWithEntity<T extends BlockEntity> extends NetworkBlock implements BlockEntityProvider {
    protected NetworkBlockWithEntity(Settings settings) {
        super(settings);
    }
    
    protected abstract BlockEntityType<T> getType();

    @Nullable
    protected final T getBlockEntity(World world, BlockPos pos) {
        return getType().get(world, pos);
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return getType().instantiate(pos, state);
    }
}
