package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

class DrawersBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public DrawersBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        valueLookupBuilder(ModTags.BlockTags.DRAWERS).add(ModBlocks.SHADOW_DRAWER, ModBlocks.COMPACTING_DRAWER, ModBlocks.COMPACTING_DRAWER, ModBlocks.SINGLE_DRAWER, ModBlocks.DOUBLE_DRAWER, ModBlocks.QUAD_DRAWER);
        valueLookupBuilder(ModTags.BlockTags.NETWORK_COMPONENTS).addTag(ModTags.BlockTags.DRAWERS).add(ModBlocks.ACCESS_POINT, ModBlocks.CONNECTOR);

        valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.SINGLE_DRAWER, ModBlocks.DOUBLE_DRAWER, ModBlocks.QUAD_DRAWER, ModBlocks.CONNECTOR);
        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.ACCESS_POINT, ModBlocks.COMPACTING_DRAWER, ModBlocks.SHADOW_DRAWER);
    }
}
