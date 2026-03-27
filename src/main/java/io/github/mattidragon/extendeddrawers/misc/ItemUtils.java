package io.github.mattidragon.extendeddrawers.misc;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemUtils {
    public static void offerOrDropStacks(Level world, BlockPos pos, @Nullable Direction side, @Nullable Player player, ItemVariant item, long amount) {
        var maxCount = item.getItem().getDefaultMaxStackSize();
        while (amount > 0) {
            int dropped = (int) Math.min(maxCount, amount);
            offerOrDrop(world, pos, side, player, item.toStack(dropped));
            amount -= dropped;
        }
    }
    
    public static void offerOrDrop(Level world, BlockPos pos, @Nullable Direction side, @Nullable Player player, ItemStack stack) {
        if (player == null) {
            int x = pos.getX() + (side == null ? 0 : side.getStepX());
            int z = pos.getZ() + (side == null ? 0 : side.getStepZ());
            world.addFreshEntity(new ItemEntity(world, x, pos.getY(), z, stack));
        } else
            player.getInventory().placeItemBackInInventory(stack);
    }
}
