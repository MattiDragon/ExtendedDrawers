package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.item.DrawerItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public class ShulkerBoxBlockEntityMixin {
    @Inject(method = "canPlaceItemThroughFace", at = @At("HEAD"), cancellable = true)
    private void fixShulkerBoxInsertion(int slot, ItemStack stack, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (stack.getItem() instanceof DrawerItem item && !item.canFitInsideContainerItems()) {
            cir.setReturnValue(false);
        }
    }
}
