package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.compacting.ServerCompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.misc.ServerRecipeManagerAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public abstract class ServerRecipeManagerMixin implements ServerCompressionRecipeManager.Provider, ServerRecipeManagerAccess {
    @Unique
    private final ServerCompressionRecipeManager compactingManager = new ServerCompressionRecipeManager((RecipeManager) (Object) this);

    @Override
    public ServerCompressionRecipeManager extended_drawers$getCompactingManager() {
        return compactingManager;
    }

    @Inject(method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void extended_drawers$reloadCompactingManager(RecipeMap preparedRecipes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        compactingManager.reload();
    }

    @Accessor
    @Override
    public abstract RecipeMap getRecipes();
}
