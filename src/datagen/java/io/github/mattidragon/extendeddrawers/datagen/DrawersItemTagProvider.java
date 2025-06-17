package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class DrawersItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public DrawersItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        copy(ModTags.BlockTags.DRAWERS, ModTags.ItemTags.DRAWERS);

        valueLookupBuilder(ModTags.ItemTags.UPGRADES).add(ModItems.T1_UPGRADE, ModItems.T2_UPGRADE, ModItems.T3_UPGRADE, ModItems.T4_UPGRADE, ModItems.CREATIVE_UPGRADE);
        valueLookupBuilder(ModTags.ItemTags.TOGGLE_HIDDEN).add(Items.INK_SAC, Items.BLACK_DYE);
        valueLookupBuilder(ModTags.ItemTags.TOGGLE_LOCK).add(ModItems.LOCK);
        valueLookupBuilder(ModTags.ItemTags.TOGGLE_VOIDING).add(Items.LAVA_BUCKET);
        valueLookupBuilder(ModTags.ItemTags.TOGGLE_DUPING).add(ModItems.DUPE_WAND);
    }
}
