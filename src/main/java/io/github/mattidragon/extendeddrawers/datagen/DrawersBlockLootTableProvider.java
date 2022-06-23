package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.misc.DrawerContentsLootFunction;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

class DrawersBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected DrawersBlockLootTableProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }
    
    @Override
    protected void generateBlockLootTables() {
        addDrop(ModBlocks.SINGLE_DRAWER, DrawersBlockLootTableProvider::drawerDrops);
        addDrop(ModBlocks.DOUBLE_DRAWER);
        addDrop(ModBlocks.QUAD_DRAWER);
        addDrop(ModBlocks.SHADOW_DRAWER);
        addDrop(ModBlocks.CONTROLLER);
        addDrop(ModBlocks.CONNECTOR);
    }
    
    private static LootTable.Builder drawerDrops(Block drop) {
        return LootTable.builder().pool(BlockLootTableGenerator.addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
                .with(ItemEntry.builder(drop)
                        .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
                        .apply(DrawerContentsLootFunction.builder()))));
    }
}
