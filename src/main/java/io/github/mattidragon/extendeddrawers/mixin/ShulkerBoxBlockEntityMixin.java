package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.item.DrawerItem;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {
    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void extended_drawers$fixShulkerBoxInsertion(int slot, ItemStack stack, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof DrawerItem item && !item.canBeNested()) {
            cir.setReturnValue(false);
        }
    }
}
