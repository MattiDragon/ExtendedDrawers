package io.github.mattidragon.extendeddrawers.item;

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
    
    public UpgradeItem(Settings settings, Identifier sprite, int multiplier) {
        this(settings, sprite, value -> value * multiplier);
    }
    
    public UpgradeItem(Settings settings, Identifier sprite, Long2LongFunction modifier) {
        super(settings);
        this.sprite = sprite;
        this.modifier = modifier;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof DrawerInteractionHandler drawer) {
            return drawer.upgrade(context.getWorld().getBlockState(context.getBlockPos()), context.getWorld(), context.getBlockPos(), context.getHitPos(), context.getSide(), context.getPlayer(), context.getStack());
        }
        return ActionResult.PASS;
    }
}
