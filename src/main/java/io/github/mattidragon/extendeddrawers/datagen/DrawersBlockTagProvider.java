package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tag.BlockTags;

class DrawersBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public DrawersBlockTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(ModTags.BlockTags.DRAWERS).add(ModBlocks.SHADOW_DRAWER, ModBlocks.SINGLE_DRAWER, ModBlocks.DOUBLE_DRAWER, ModBlocks.QUAD_DRAWER);
        getOrCreateTagBuilder(ModTags.BlockTags.NETWORK_COMPONENTS).addTag(ModTags.BlockTags.DRAWERS).add(ModBlocks.CONTROLLER, ModBlocks.CONNECTOR);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(ModBlocks.SINGLE_DRAWER, ModBlocks.DOUBLE_DRAWER, ModBlocks.QUAD_DRAWER, ModBlocks.CONNECTOR);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ModBlocks.CONTROLLER, ModBlocks.SHADOW_DRAWER);
    }
}
