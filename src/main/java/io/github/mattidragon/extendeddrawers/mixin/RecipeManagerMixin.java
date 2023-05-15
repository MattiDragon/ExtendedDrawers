package io.github.mattidragon.extendeddrawers.mixin;

import com.google.gson.JsonElement;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin implements CompressionRecipeManager.Provider {
    private final CompressionRecipeManager compactingManager = new CompressionRecipeManager((RecipeManager)(Object)this);

    @Override
    public CompressionRecipeManager extended_drawers$getCompactingManager() {
        return compactingManager;
    }

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("TAIL"))
    private void extended_drawers$reloadCompactingManager(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        compactingManager.reload();
    }
}
