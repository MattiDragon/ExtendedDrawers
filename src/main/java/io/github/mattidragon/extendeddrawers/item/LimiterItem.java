package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtElement;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LimiterItem extends Item {
    public LimiterItem(Settings settings) {
        super(settings);
    }

    @Nullable
    public static Long getLimit(ItemStack stack) {
        var stackNbt = stack.getNbt();
        if (stackNbt == null || !stackNbt.contains("limit", NbtElement.NUMBER_TYPE)) {
            return null;
        } else {
            return stackNbt.getLong("limit");
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        var stackNbt = stack.getNbt();
        Text limitText;
        if (stackNbt == null || !stackNbt.contains("limit", NbtElement.NUMBER_TYPE)) {
            limitText = Text.translatable("item.extended_drawers.limiter.unset").formatted(Formatting.ITALIC);
        } else {
            limitText = Text.literal(String.valueOf(stackNbt.getLong("limit")));
        }
        tooltip.add(Text.translatable("item.extended_drawers.limiter.limit", limitText).formatted(Formatting.GRAY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);
        user.useBook(itemStack, hand);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof DrawerInteractionHandler drawer) {
            return drawer.changeLimiter(context.getWorld().getBlockState(context.getBlockPos()), context.getWorld(), context.getBlockPos(), context.getHitPos(), context.getSide(), context.getPlayer(), context.getStack());
        }
        return ActionResult.PASS;
    }
}
