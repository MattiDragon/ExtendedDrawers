package io.github.mattidragon.extendeddrawers.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mattidragon.extendeddrawers.compacting.CompressionOverrideLoader;
import io.github.mattidragon.extendeddrawers.compacting.ServerCompressionRecipeManager;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ServerDynamicRegistryType;
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

@Mixin(value = DataPackContents.class)
public class DataPackContentsMixin {
    @Shadow @Final private ServerRecipeManager recipeManager;

    @Unique
    private CompressionOverrideLoader extended_drawers$compressionOverrideLoader;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void extend_drawers$setupCompressionOverrideLoader(CombinedDynamicRegistries<ServerDynamicRegistryType> dynamicRegistries, RegistryWrapper.WrapperLookup registries, FeatureSet enabledFeatures, CommandManager.RegistrationEnvironment environment, List<Registry.PendingTagLoad<?>> pendingTagLoads, int functionPermissionLevel, CallbackInfo ci) {
        extended_drawers$compressionOverrideLoader = new CompressionOverrideLoader(ServerCompressionRecipeManager.of(recipeManager));
    }

    @ModifyReturnValue(method = "getContents", at = @At("RETURN"))
    private List<ResourceReloader> extend_drawers$injectCompressionOverrideLoader(List<ResourceReloader> original) {
        return ImmutableList.<ResourceReloader>builder().addAll(original).add(extended_drawers$compressionOverrideLoader).build();
    }
}
