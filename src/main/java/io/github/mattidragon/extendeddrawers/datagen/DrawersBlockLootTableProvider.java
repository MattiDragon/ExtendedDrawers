package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

class DrawersBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected DrawersBlockLootTableProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }
    
    @Override
    protected void generateBlockLootTables() {
        addDrop(ModBlocks.SINGLE_DRAWER);
        addDrop(ModBlocks.DOUBLE_DRAWER);
        addDrop(ModBlocks.QUAD_DRAWER);
        addDrop(ModBlocks.SHADOW_DRAWER);
        addDrop(ModBlocks.CONTROLLER);
    }
}
