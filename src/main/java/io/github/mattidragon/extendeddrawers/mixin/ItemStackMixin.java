package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean isIn(TagKey<Item> tag);

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
        }
    }
}
