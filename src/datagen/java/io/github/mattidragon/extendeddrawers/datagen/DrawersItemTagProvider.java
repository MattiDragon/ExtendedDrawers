package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.item.ModItemIds;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.ItemIds;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class DrawersItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public DrawersItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture, @Nullable BlockTagsProvider blockTagsProvider) {
        super(output, registryLookupFuture, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        copy(ModTags.BlockTags.DRAWERS, ModTags.ItemTags.DRAWERS);

        builder(ModTags.ItemTags.UPGRADES).add(ModItemIds.T1_UPGRADE, ModItemIds.T2_UPGRADE, ModItemIds.T3_UPGRADE, ModItemIds.T4_UPGRADE, ModItemIds.CREATIVE_UPGRADE);
        builder(ModTags.ItemTags.TOGGLE_HIDDEN).add(ItemIds.INK_SAC, ItemIds.DYE.black());
        builder(ModTags.ItemTags.TOGGLE_LOCK).add(ModItemIds.LOCK);
        builder(ModTags.ItemTags.TOGGLE_VOIDING).add(ItemIds.LAVA_BUCKET);
        builder(ModTags.ItemTags.TOGGLE_DUPING).add(ModItemIds.DUPE_WAND);
    }
}
