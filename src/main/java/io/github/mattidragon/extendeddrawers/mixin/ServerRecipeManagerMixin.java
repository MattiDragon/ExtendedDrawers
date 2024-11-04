package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.compacting.ServerCompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.misc.ServerRecipeManagerAccess;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerRecipeManager.class)
public abstract class ServerRecipeManagerMixin implements ServerCompressionRecipeManager.Provider, ServerRecipeManagerAccess {
    @Unique
    private final ServerCompressionRecipeManager compactingManager = new ServerCompressionRecipeManager((ServerRecipeManager) (Object) this);

    @Override
    public ServerCompressionRecipeManager extended_drawers$getCompactingManager() {
        return compactingManager;
    }

    @Inject(method = "apply(Lnet/minecraft/recipe/PreparedRecipes;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("TAIL"))
    private void extended_drawers$reloadCompactingManager(PreparedRecipes preparedRecipes, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        compactingManager.reload();
    }

    @Accessor
    @Override
    public abstract PreparedRecipes getPreparedRecipes();
}
