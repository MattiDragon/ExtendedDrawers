package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import it.unimi.dsi.fastutil.longs.Long2LongFunction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.LongUnaryOperator;

public class UpgradeItem extends Item {
    public final Identifier sprite;
    public final LongUnaryOperator modifier;
    
    public UpgradeItem(Properties settings, Identifier sprite, int tier) {
        this(settings, sprite, value -> value * getMultiplier(tier));
    }
    
    public UpgradeItem(Properties settings, Identifier sprite, Long2LongFunction modifier) {
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
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof DrawerInteractionHandler drawer) {
            return drawer.changeUpgrade(context.getLevel().getBlockState(context.getClickedPos()), context.getLevel(), context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
        }
        return InteractionResult.PASS;
    }
}
