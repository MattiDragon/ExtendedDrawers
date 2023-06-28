package io.github.mattidragon.extendeddrawers.block.base;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface DrawerInteractionHandler {
    default ActionResult toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        return ActionResult.PASS;
    }

    default ActionResult toggleVoid(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        return ActionResult.PASS;
    }

    default ActionResult toggleHide(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        return ActionResult.PASS;
    }

    default ActionResult toggleDuping(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        return ActionResult.PASS;
    }

    default ActionResult changeUpgrade(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, @Nullable PlayerEntity player, ItemStack stack) {
        return ActionResult.PASS;
    }

    default ActionResult changeLimiter(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, @Nullable PlayerEntity player, ItemStack stack) {
        return ActionResult.PASS;
    }
}
