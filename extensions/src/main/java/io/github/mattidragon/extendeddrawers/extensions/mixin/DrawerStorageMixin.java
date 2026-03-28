package io.github.mattidragon.extendeddrawers.extensions.mixin;

import io.github.mattidragon.extendeddrawers.extensions.storage.DrawerBarrelStorage;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrawerStorage.class)
public interface DrawerStorageMixin {
    @Inject(method = "compareTo(Lio/github/mattidragon/extendeddrawers/storage/DrawerStorage;)I",
            at = @At("HEAD"),
            cancellable = true)
    private void injectDrawerBarrelSorting(DrawerStorage other, CallbackInfoReturnable<Integer> cir) {
        if (this instanceof DrawerBarrelStorage != other instanceof DrawerBarrelStorage) {
            cir.setReturnValue(this instanceof DrawerBarrelStorage ? 1 : -1);
        }
    }
}
