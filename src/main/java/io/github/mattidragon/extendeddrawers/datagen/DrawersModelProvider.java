package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;

import java.util.Optional;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

class DrawersModelProvider extends FabricModelProvider {
    public DrawersModelProvider(FabricDataGenerator generator) {
        super(generator);
    }
    
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(ModBlocks.ACCESS_POINT);
        generator.registerSingleton(ModBlocks.CONNECTOR, TextureMap.all(id("block/drawer_base")), Models.CUBE_ALL);

        registerDrawerModel(ModBlocks.SINGLE_DRAWER, generator);
        registerDrawerModel(ModBlocks.DOUBLE_DRAWER, generator);
        registerDrawerModel(ModBlocks.QUAD_DRAWER, generator);

        generator.registerNorthDefaultHorizontalRotatable(ModBlocks.SHADOW_DRAWER,
                TextureMap.sideEnd(id("block/shadow_drawer_side"), id("block/shadow_drawer_side")));

        generateCompactingDrawerModel(generator);
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

    private void generateCompactingDrawerModel(BlockStateModelGenerator generator) {
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.COMPACTING_DRAWER, BlockStateVariant.create().put(VariantSettings.MODEL, id("block/compacting_drawer"))).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }

    private void registerDrawerModel(Block block, BlockStateModelGenerator generator) {
        var template = new Model(Optional.of(id("drawer_template")), Optional.empty(), TextureKey.FRONT);

        var model = template.upload(block, TextureMap.of(TextureKey.FRONT, ModelIds.getBlockModelId(block)), generator.modelCollector);
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model)).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
    }
}
