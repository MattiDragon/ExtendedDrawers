package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

class DrawersBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public DrawersBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        valueLookupBuilder(ModTags.BlockTags.DRAWERS).add(ModBlocks.SHADOW_DRAWER, ModBlocks.COMPACTING_DRAWER, ModBlocks.COMPACTING_DRAWER, ModBlocks.SINGLE_DRAWER, ModBlocks.DOUBLE_DRAWER, ModBlocks.QUAD_DRAWER);
        valueLookupBuilder(ModTags.BlockTags.NETWORK_COMPONENTS).addTag(ModTags.BlockTags.DRAWERS).add(ModBlocks.ACCESS_POINT, ModBlocks.CONNECTOR);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.SINGLE_DRAWER, ModBlocks.DOUBLE_DRAWER, ModBlocks.QUAD_DRAWER, ModBlocks.CONNECTOR);
        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ACCESS_POINT, ModBlocks.COMPACTING_DRAWER, ModBlocks.SHADOW_DRAWER);
    }
}
