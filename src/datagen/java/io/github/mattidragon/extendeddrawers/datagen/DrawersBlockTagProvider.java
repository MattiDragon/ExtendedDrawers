package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.block.ModBlockItemIds;
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
        builder(ModTags.BlockTags.DRAWERS).add(ModBlockItemIds.SHADOW_DRAWER, ModBlockItemIds.COMPACTING_DRAWER, ModBlockItemIds.COMPACTING_DRAWER, ModBlockItemIds.SINGLE_DRAWER, ModBlockItemIds.DOUBLE_DRAWER, ModBlockItemIds.QUAD_DRAWER);
        builder(ModTags.BlockTags.NETWORK_COMPONENTS).addTag(ModTags.BlockTags.DRAWERS).add(ModBlockItemIds.ACCESS_POINT, ModBlockItemIds.CONNECTOR);

        builder(BlockTags.MINEABLE_WITH_AXE).add(ModBlockItemIds.SINGLE_DRAWER, ModBlockItemIds.DOUBLE_DRAWER, ModBlockItemIds.QUAD_DRAWER, ModBlockItemIds.CONNECTOR);
        builder(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlockItemIds.ACCESS_POINT, ModBlockItemIds.COMPACTING_DRAWER, ModBlockItemIds.SHADOW_DRAWER);
    }
}
