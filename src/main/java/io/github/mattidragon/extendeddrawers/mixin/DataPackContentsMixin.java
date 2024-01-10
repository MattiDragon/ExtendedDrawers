package io.github.mattidragon.extendeddrawers.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mattidragon.extendeddrawers.compacting.CompressionOverrideLoader;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// We lower our priority to run before QSL because they have an unconditional return in their getContents mixin.
// This is a temporary workaround until they switch to using mixin extras
// See: https://github.com/QuiltMC/quilt-standard-libraries/issues/358
@Mixin(value = DataPackContents.class, priority = 900)
public class DataPackContentsMixin {
    @Shadow @Final private RecipeManager recipeManager;

    @Unique
    private CompressionOverrideLoader extended_drawers$compressionOverrideLoader;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void extend_drawers$setupCompressionOverrideLoader(DynamicRegistryManager.Immutable dynamicRegistryManager, FeatureSet enabledFeatures, CommandManager.RegistrationEnvironment environment, int functionPermissionLevel, CallbackInfo ci) {
        extended_drawers$compressionOverrideLoader = new CompressionOverrideLoader(CompressionRecipeManager.of(recipeManager));
    }

    @ModifyReturnValue(method = "getContents", at = @At("RETURN"))
    private List<ResourceReloader> extend_drawers$injectCompressionOverrideLoader(List<ResourceReloader> original) {
        return ImmutableList.<ResourceReloader>builder().addAll(original).add(extended_drawers$compressionOverrideLoader).build();
    }
}
