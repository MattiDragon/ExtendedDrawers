package io.github.mattidragon.extendeddrawers.misc;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ItemUtils {
    public static void offerOrDropStacks(World world, BlockPos pos, Direction side, @Nullable PlayerEntity player, ItemVariant item, long amount) {
        var maxCount = item.getItem().getMaxCount();
        while (amount > 0) {
            int dropped = (int) Math.min(maxCount, amount);
            offerOrDrop(world, pos, side, player, item.toStack(dropped));
            amount -= dropped;
        }
    }
    
    public static void offerOrDrop(World world, BlockPos pos, Direction side, @Nullable PlayerEntity player, ItemStack stack) {
        if (player == null)
            world.spawnEntity(new ItemEntity(world, pos.getX() + side.getOffsetX(), pos.getY(), pos.getZ() + side.getOffsetZ(), stack));
        else
            player.getInventory().offerOrDrop(stack);
    }
}
