package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean isIn(TagKey<Item> tag);

    @Shadow public abstract <T extends TooltipAppender> void appendComponentTooltip(ComponentType<T> componentType, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type);

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"), cancellable = true)
    private void extended_drawers$applyModifiers(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        var world = context.getWorld();
        var state = world.getBlockState(context.getBlockPos());

        if (state.getBlock() instanceof DrawerInteractionHandler handler) {
            if (isIn(ModTags.ItemTags.TOGGLE_VOIDING)) {
                var result = handler.toggleVoid(state, world, context.getBlockPos(), context.getHitPos(), context.getSide());
                if (result != ActionResult.PASS)
                    cir.setReturnValue(result);
            }
            if (isIn(ModTags.ItemTags.TOGGLE_HIDDEN)) {
                var result = handler.toggleHide(state, world, context.getBlockPos(), context.getHitPos(), context.getSide());
                if (result != ActionResult.PASS)
                    cir.setReturnValue(result);
            }
            if (isIn(ModTags.ItemTags.TOGGLE_LOCK)) {
                var result = handler.toggleLock(state, world, context.getBlockPos(), context.getHitPos(), context.getSide());
                if (result != ActionResult.PASS)
                    cir.setReturnValue(result);
            }
            if (isIn(ModTags.ItemTags.TOGGLE_DUPING)) {
                var result = handler.toggleDuping(state, world, context.getBlockPos(), context.getHitPos(), context.getSide());
                if (result != ActionResult.PASS)
                    cir.setReturnValue(result);
            }
        }
    }

    @Inject(method = "appendTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    shift = At.Shift.AFTER))
    private void injectTooltips(Item.TooltipContext context, TooltipDisplayComponent displayComponent, PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
        appendComponentTooltip(ModDataComponents.LIMITER_LIMIT, context, displayComponent, textConsumer, type);
        appendComponentTooltip(ModDataComponents.COMPACTING_DRAWER_CONTENTS, context, displayComponent, textConsumer, type);
        appendComponentTooltip(ModDataComponents.DRAWER_CONTENTS, context, displayComponent, textConsumer, type);
    }
}
