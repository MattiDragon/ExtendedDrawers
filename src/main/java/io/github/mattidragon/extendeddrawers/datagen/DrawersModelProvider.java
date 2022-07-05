package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureMap;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

class DrawersModelProvider extends FabricModelProvider {
    public DrawersModelProvider(FabricDataGenerator generator) {
        super(generator);
    }
    
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(ModBlocks.ACCESS_POINT);

        generator.registerSingleton(ModBlocks.CONNECTOR, TextureMap.all(id("block/drawer_base")), Models.CUBE_ALL);
        
        var texture = TextureMap.sideEnd(id("block/drawer_base"), id("block/drawer_base"));
        generator.registerNorthDefaultHorizontalRotatable(ModBlocks.SINGLE_DRAWER, texture);
        generator.registerNorthDefaultHorizontalRotatable(ModBlocks.DOUBLE_DRAWER, texture);
        generator.registerNorthDefaultHorizontalRotatable(ModBlocks.QUAD_DRAWER, texture);
        
        generator.registerNorthDefaultHorizontalRotatable(ModBlocks.SHADOW_DRAWER,
                TextureMap.sideEnd(id("block/shadow_drawer_side"), id("block/shadow_drawer_side")));
    }
    
    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(ModItems.T1_UPGRADE, Models.GENERATED);
        generator.register(ModItems.T2_UPGRADE, Models.GENERATED);
        generator.register(ModItems.T3_UPGRADE, Models.GENERATED);
        generator.register(ModItems.T4_UPGRADE, Models.GENERATED);
        generator.register(ModItems.DOWNGRADE, Models.GENERATED);
        generator.register(ModItems.CREATIVE_UPGRADE, Models.GENERATED);
        generator.register(ModItems.LOCK, Models.GENERATED);
        generator.register(ModItems.UPGRADE_FRAME, Models.GENERATED);

    }
}
