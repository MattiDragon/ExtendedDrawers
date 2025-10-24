package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

class ExtensionsBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ExtensionsBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        valueLookupBuilder(ModTags.BlockTags.DRAWERS).add(ExtensionBlocks.DRAWER_BARREL);
        valueLookupBuilder(ModTags.BlockTags.NETWORK_COMPONENTS).add(ExtensionBlocks.ENDER_CONNECTOR);

        valueLookupBuilder(BlockTags.AXE_MINEABLE).add(ExtensionBlocks.DRAWER_BARREL);
    }
}
