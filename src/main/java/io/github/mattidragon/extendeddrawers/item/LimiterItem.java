package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.component.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class LimiterItem extends Item {
    public LimiterItem(Properties properties) {
        super(properties);
    }

    // Mojang have deprecated tooltips from items in favour of tooltips from components.
    // This does not work here as we need a tooltip from the lack of a component.
    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        if (display.shows(ModDataComponents.LIMITER_LIMIT) && itemStack.get(ModDataComponents.LIMITER_LIMIT) == null) {
            builder.accept(Component.translatable("item.extended_drawers.limiter.unset").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        player.openItemGui(itemStack, hand);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof DrawerInteractionHandler drawer) {
            return drawer.changeLimiter(context.getLevel().getBlockState(context.getClickedPos()), context.getLevel(), context.getClickedPos(), context.getClickLocation(), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
        }
        return InteractionResult.PASS;
    }
}
