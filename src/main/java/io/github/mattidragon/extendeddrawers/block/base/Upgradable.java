package io.github.mattidragon.extendeddrawers.block.base;

import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface Upgradable {
    ActionResult upgrade(UpgradeItem upgrade, BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, @Nullable PlayerEntity player, ItemStack stack);
}
