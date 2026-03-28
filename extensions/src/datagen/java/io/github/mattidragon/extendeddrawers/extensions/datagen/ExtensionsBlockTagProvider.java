package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
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
        valueLookupBuilder(ModTags.BlockTags.DRAWERS).add(ExtensionBlocks.DRAWER_BARREL);
        valueLookupBuilder(ModTags.BlockTags.NETWORK_COMPONENTS).add(ExtensionBlocks.ENDER_CONNECTOR);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE).add(ExtensionBlocks.DRAWER_BARREL);
    }
}
