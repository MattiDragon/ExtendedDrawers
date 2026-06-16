package io.github.mattidragon.extendeddrawers.misc;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.component.ModDataComponents;
import io.github.mattidragon.extendeddrawers.item.DrawerItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ItemUtils {
    public static void offerOrDropStacks(Level level, BlockPos pos, @Nullable Direction side, @Nullable Player player, ItemVariant item, long amount) {
        var maxCount = item.getItem().getDefaultMaxStackSize();
        while (amount > 0) {
            int dropped = (int) Math.min(maxCount, amount);
            offerOrDrop(level, pos, side, player, item.toStack(dropped));
            amount -= dropped;
        }
    }
    
    public static void offerOrDrop(Level level, BlockPos pos, @Nullable Direction side, @Nullable Player player, ItemStack stack) {
        if (player == null) {
            int x = pos.getX() + (side == null ? 0 : side.getStepX());
            int z = pos.getZ() + (side == null ? 0 : side.getStepZ());
            level.addFreshEntity(new ItemEntity(level, x, pos.getY(), z, stack));
        } else
            player.getInventory().placeItemBackInInventory(stack);
    }

    public static boolean canStoreInDrawer(ItemVariant variant) {
        if (ExtendedDrawers.CONFIG.get().misc().allowRecursion()) {
            return true;
        }
        if (variant.getItem().canFitInsideContainerItems()) {
            return true;
        }
        if (variant.getItem() instanceof DrawerItem) {
            var components = variant.getComponents();
            return components.get(ModDataComponents.DRAWER_CONTENTS) == null
                   && components.get(ModDataComponents.COMPACTING_DRAWER_CONTENTS) == null;
        }
        return false;
    }
}
