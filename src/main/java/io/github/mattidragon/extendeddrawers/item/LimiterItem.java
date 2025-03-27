package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class LimiterItem extends Item {
    public LimiterItem(Settings settings) {
        super(settings);
    }

    // Mojang have deprecated tooltips from items in favour of tooltips from components.
    // This does not work here as we need a tooltip from the lack of a component.
    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        if (displayComponent.shouldDisplay(ModDataComponents.LIMITER_LIMIT) && stack.get(ModDataComponents.LIMITER_LIMIT) == null) {
            textConsumer.accept(Text.translatable("item.extended_drawers.limiter.unset").formatted(Formatting.ITALIC, Formatting.GRAY));
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);
        user.useBook(itemStack, hand);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof DrawerInteractionHandler drawer) {
            return drawer.changeLimiter(context.getWorld().getBlockState(context.getBlockPos()), context.getWorld(), context.getBlockPos(), context.getHitPos(), context.getSide(), context.getPlayer(), context.getStack());
        }
        return ActionResult.PASS;
    }
}
