package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.extensions.block.ExtensionBlockItemIds;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

class ExtensionsBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ExtensionsBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(ModTags.BlockTags.DRAWERS).add(ExtensionBlockItemIds.DRAWER_BARREL);
        builder(ModTags.BlockTags.NETWORK_COMPONENTS).add(ExtensionBlockItemIds.ENDER_CONNECTOR);

        builder(BlockTags.MINEABLE_WITH_AXE).add(ExtensionBlockItemIds.DRAWER_BARREL);
    }
}
