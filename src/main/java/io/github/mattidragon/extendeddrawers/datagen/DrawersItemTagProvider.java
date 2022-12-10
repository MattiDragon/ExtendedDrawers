package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class DrawersItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public DrawersItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ModTags.ItemTags.DRAWERS).add(ModItems.SHADOW_DRAWER, ModItems.SINGLE_DRAWER, ModItems.DOUBLE_DRAWER, ModItems.QUAD_DRAWER);
        getOrCreateTagBuilder(ModTags.ItemTags.UPGRADES).add(ModItems.T1_UPGRADE, ModItems.T2_UPGRADE, ModItems.T3_UPGRADE, ModItems.T4_UPGRADE, ModItems.CREATIVE_UPGRADE, ModItems.DOWNGRADE);
    }
}
