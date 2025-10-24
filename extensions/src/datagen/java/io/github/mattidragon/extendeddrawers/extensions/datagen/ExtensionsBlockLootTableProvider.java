package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

class ExtensionsBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ExtensionsBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ExtensionBlocks.DRAWER_BARREL);
        addDrop(ExtensionBlocks.ENDER_CONNECTOR);
    }
}
