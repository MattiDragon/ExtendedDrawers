package io.github.mattidragon.extendeddrawers.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface DrawerInteractionHandler {
    default InteractionResult toggleLock(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        return InteractionResult.PASS;
    }

    default InteractionResult toggleVoid(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        return InteractionResult.PASS;
    }

    default InteractionResult toggleHide(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        return InteractionResult.PASS;
    }

    default InteractionResult toggleDuping(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        return InteractionResult.PASS;
    }

    default InteractionResult changeUpgrade(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side, @Nullable Player player, ItemStack stack) {
        return InteractionResult.PASS;
    }

    default InteractionResult changeLimiter(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side, @Nullable Player player, ItemStack stack) {
        return InteractionResult.PASS;
    }
}
