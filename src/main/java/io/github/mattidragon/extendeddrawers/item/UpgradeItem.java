package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.function.LongUnaryOperator;

public class UpgradeItem extends Item {
    public final Identifier sprite;
    public final LongUnaryOperator modifier;
    
    public UpgradeItem(Settings settings, Identifier sprite, int tier) {
        this(settings, sprite, value -> value * getMultiplier(tier));
    }
    
    public UpgradeItem(Settings settings, Identifier sprite, Long2LongFunction modifier) {
        super(settings);
        this.sprite = sprite;
        this.modifier = modifier;
    }

    private static int getMultiplier(int tier) {
        var config = ExtendedDrawers.CONFIG.get().storage();
        return switch (tier) {
            case 1 -> config.t1UpgradeMultiplier();
            case 2 -> config.t2UpgradeMultiplier();
            case 3 -> config.t3UpgradeMultiplier();
            case 4 -> config.t4UpgradeMultiplier();
            default -> 1;
        };
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof DrawerInteractionHandler drawer) {
            return drawer.changeUpgrade(context.getWorld().getBlockState(context.getBlockPos()), context.getWorld(), context.getBlockPos(), context.getHitPos(), context.getSide(), context.getPlayer(), context.getStack());
        }
        return ActionResult.PASS;
    }
}
