package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.block.base.Upgradable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

public class UpgradeItem extends Item {
    public final Identifier sprite;
    public final double multiplier;
    
    public UpgradeItem(Settings settings, Identifier sprite, double multiplier) {
        super(settings);
        this.sprite = sprite;
        this.multiplier = multiplier;
    }
    
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof Upgradable upgradable) {
            return upgradable.upgrade(this, context.getWorld().getBlockState(context.getBlockPos()), context.getWorld(), context.getBlockPos(), context.getHitPos(), context.getSide(), context.getPlayer(), context.getStack());
        }
        return ActionResult.PASS;
    }
}
