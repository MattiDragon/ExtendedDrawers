package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class DrawersItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public DrawersItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture, @Nullable BlockTagsProvider blockTagsProvider) {
        super(output, registryLookupFuture, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        copy(ModTags.BlockTags.DRAWERS, ModTags.ItemTags.DRAWERS);

        valueLookupBuilder(ModTags.ItemTags.UPGRADES).add(ModItems.T1_UPGRADE, ModItems.T2_UPGRADE, ModItems.T3_UPGRADE, ModItems.T4_UPGRADE, ModItems.CREATIVE_UPGRADE);
        valueLookupBuilder(ModTags.ItemTags.TOGGLE_HIDDEN).add(Items.INK_SAC, Items.BLACK_DYE);
        valueLookupBuilder(ModTags.ItemTags.TOGGLE_LOCK).add(ModItems.LOCK);
        valueLookupBuilder(ModTags.ItemTags.TOGGLE_VOIDING).add(Items.LAVA_BUCKET);
        valueLookupBuilder(ModTags.ItemTags.TOGGLE_DUPING).add(ModItems.DUPE_WAND);
    }
}
