package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.component.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemInstance {
    @Shadow public abstract <T extends TooltipProvider> void addToTooltip(DataComponentType<T> componentType, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type);

    @Shadow
    public abstract boolean is(Predicate<Holder<Item>> item);

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    private void extended_drawers$applyModifiers(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        var level = context.getLevel();
        var state = level.getBlockState(context.getClickedPos());

        if (state.getBlock() instanceof DrawerInteractionHandler handler) {
            if (is(ModTags.ItemTags.TOGGLE_VOIDING)) {
                var result = handler.toggleVoid(state, level, context.getClickedPos(), context.getClickLocation(), context.getClickedFace());
                if (result != InteractionResult.PASS)
                    cir.setReturnValue(result);
            }
            if (is(ModTags.ItemTags.TOGGLE_HIDDEN)) {
                var result = handler.toggleHide(state, level, context.getClickedPos(), context.getClickLocation(), context.getClickedFace());
                if (result != InteractionResult.PASS)
                    cir.setReturnValue(result);
            }
            if (is(ModTags.ItemTags.TOGGLE_LOCK)) {
                var result = handler.toggleLock(state, level, context.getClickedPos(), context.getClickLocation(), context.getClickedFace());
                if (result != InteractionResult.PASS)
                    cir.setReturnValue(result);
            }
            if (is(ModTags.ItemTags.TOGGLE_DUPING)) {
                var result = handler.toggleDuping(state, level, context.getClickedPos(), context.getClickLocation(), context.getClickedFace());
                if (result != InteractionResult.PASS)
                    cir.setReturnValue(result);
            }
        }
    }

    @Inject(method = "addDetailsToTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V",
                    shift = At.Shift.AFTER))
    private void injectTooltips(Item.TooltipContext context, TooltipDisplay displayComponent, Player player, TooltipFlag type, Consumer<Component> textConsumer, CallbackInfo ci) {
        addToTooltip(ModDataComponents.LIMITER_LIMIT, context, displayComponent, textConsumer, type);
        addToTooltip(ModDataComponents.COMPACTING_DRAWER_CONTENTS, context, displayComponent, textConsumer, type);
        addToTooltip(ModDataComponents.DRAWER_CONTENTS, context, displayComponent, textConsumer, type);
    }
}
