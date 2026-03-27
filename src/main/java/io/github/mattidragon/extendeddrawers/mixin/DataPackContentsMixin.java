package io.github.mattidragon.extendeddrawers.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.mattidragon.extendeddrawers.compacting.CompressionOverrideLoader;
import io.github.mattidragon.extendeddrawers.compacting.ServerCompressionRecipeManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ReloadableServerResources.class)
public class DataPackContentsMixin {
    @Shadow @Final private RecipeManager recipes;

    @Unique
    private CompressionOverrideLoader extended_drawers$compressionOverrideLoader;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void extend_drawers$setupCompressionOverrideLoader(LayeredRegistryAccess<RegistryLayer> dynamicRegistries, HolderLookup.Provider registries, FeatureFlagSet enabledFeatures, Commands.CommandSelection environment, List<Registry.PendingTags<?>> pendingTagLoads, PermissionSet permissions, CallbackInfo ci) {
        extended_drawers$compressionOverrideLoader = new CompressionOverrideLoader(ServerCompressionRecipeManager.of(recipes));
    }

    @ModifyReturnValue(method = "listeners", at = @At("RETURN"))
    private List<PreparableReloadListener> extend_drawers$injectCompressionOverrideLoader(List<PreparableReloadListener> original) {
        return ImmutableList.<PreparableReloadListener>builder().addAll(original).add(extended_drawers$compressionOverrideLoader).build();
    }
}
