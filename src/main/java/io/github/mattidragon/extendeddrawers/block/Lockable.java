package io.github.mattidragon.extendeddrawers.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface Lockable {
    void toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side);
}
