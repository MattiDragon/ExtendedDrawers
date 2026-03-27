package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;

class DrawersBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected DrawersBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        add(ModBlocks.SINGLE_DRAWER, this::drawerDrops);
        add(ModBlocks.DOUBLE_DRAWER, this::drawerDrops);
        add(ModBlocks.QUAD_DRAWER, this::drawerDrops);
        add(ModBlocks.COMPACTING_DRAWER, this::drawerDrops);
        dropSelf(ModBlocks.SHADOW_DRAWER);
        dropSelf(ModBlocks.ACCESS_POINT);
        dropSelf(ModBlocks.CONNECTOR);
    }
    
    private LootTable.Builder drawerDrops(Block drop) {
        return LootTable.lootTable().withPool(applyExplosionCondition(drop, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f))
                .add(LootItem.lootTableItem(drop)
                        .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                .include(ModDataComponents.DRAWER_CONTENTS)
                                .include(ModDataComponents.COMPACTING_DRAWER_CONTENTS)))));
    }
}
