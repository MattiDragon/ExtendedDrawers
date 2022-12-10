package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.misc.DrawerContentsLootFunction;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

class DrawersBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected DrawersBlockLootTableProvider(FabricDataOutput output) {
        super(output);
    }
    
    @Override
    public void generate() {
        addDrop(ModBlocks.SINGLE_DRAWER, this::drawerDrops);
        addDrop(ModBlocks.DOUBLE_DRAWER, this::drawerDrops);
        addDrop(ModBlocks.QUAD_DRAWER, this::drawerDrops);
        addDrop(ModBlocks.SHADOW_DRAWER);
        addDrop(ModBlocks.ACCESS_POINT);
        addDrop(ModBlocks.CONNECTOR);
    }
    
    private LootTable.Builder drawerDrops(Block drop) {
        return LootTable.builder().pool(addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
                .with(ItemEntry.builder(drop)
                        .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
                        .apply(DrawerContentsLootFunction.builder()))));
    }
}
