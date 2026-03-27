package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ExtensionsItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ExtensionsItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        copy(ModTags.BlockTags.DRAWERS, ModTags.ItemTags.DRAWERS);
    }
}
