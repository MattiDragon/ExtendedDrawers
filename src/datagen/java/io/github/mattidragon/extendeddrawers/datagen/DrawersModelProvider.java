package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

import java.util.Optional;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

class DrawersModelProvider extends FabricModelProvider {
    public DrawersModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        generator.registerSimpleCubeAll(ModBlocks.ACCESS_POINT);
        generator.registerSingleton(ModBlocks.CONNECTOR, block -> TexturedModel.getCubeAll(id("block/drawer_base")));

        registerDrawerModel(ModBlocks.SINGLE_DRAWER, generator);
        registerDrawerModel(ModBlocks.DOUBLE_DRAWER, generator);
        registerDrawerModel(ModBlocks.QUAD_DRAWER, generator);

        generateShadowDrawerModel(generator);
        generateCompactingDrawerModel(generator);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(ModItems.T1_UPGRADE, Models.GENERATED);
        generator.register(ModItems.T2_UPGRADE, Models.GENERATED);
        generator.register(ModItems.T3_UPGRADE, Models.GENERATED);
        generator.register(ModItems.T4_UPGRADE, Models.GENERATED);
        generator.register(ModItems.CREATIVE_UPGRADE, Models.GENERATED);
        generator.register(ModItems.LOCK, Models.GENERATED);
        generator.register(ModItems.UPGRADE_FRAME, Models.GENERATED);
        generator.register(ModItems.LIMITER, Models.GENERATED);
        generator.register(ModItems.DUPE_WAND, Models.GENERATED);
    }

    private static void generateShadowDrawerModel(BlockStateModelGenerator generator) {
        var modelId = Models.ORIENTABLE.upload(ModBlocks.SHADOW_DRAWER, TextureMap.sideEnd(id("block/shadow_drawer_side"), id("block/shadow_drawer_side")).copyAndAdd(TextureKey.FRONT, TextureMap.getId(ModBlocks.SHADOW_DRAWER)), generator.modelCollector);
        generator.blockStateCollector.accept(
                VariantsBlockModelDefinitionCreator.of(
                                ModBlocks.SHADOW_DRAWER,
                                BlockStateModelGenerator.createWeightedVariant(modelId))
                        .coordinate(getBlockStateMap()));
    }

    private void generateCompactingDrawerModel(BlockStateModelGenerator generator) {
        generator.blockStateCollector.accept(
                VariantsBlockModelDefinitionCreator.of(
                                ModBlocks.COMPACTING_DRAWER,
                                BlockStateModelGenerator.createWeightedVariant(id("block/compacting_drawer")))
                        .coordinate(getBlockStateMap()));
    }

    private void registerDrawerModel(Block block, BlockStateModelGenerator generator) {
        var template = new Model(Optional.of(id("drawer_template")), Optional.empty(), TextureKey.FRONT);

        var model = template.upload(block, TextureMap.of(TextureKey.FRONT, ModelIds.getBlockModelId(block)), generator.modelCollector);
        generator.blockStateCollector.accept(VariantsBlockModelDefinitionCreator.of(block, BlockStateModelGenerator.createWeightedVariant(model)).coordinate(getBlockStateMap()));
    }

    private static BlockStateVariantMap<ModelVariantOperator> getBlockStateMap() {
        return BlockStateVariantMap.operations(Properties.BLOCK_FACE, Properties.HORIZONTAL_FACING)
                .register(BlockFace.FLOOR, Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90.then(BlockStateModelGenerator.ROTATE_X_270))
                .register(BlockFace.FLOOR, Direction.WEST, BlockStateModelGenerator.ROTATE_Y_270.then(BlockStateModelGenerator.ROTATE_X_270))
                .register(BlockFace.FLOOR, Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_180.then(BlockStateModelGenerator.ROTATE_X_270))
                .register(BlockFace.FLOOR, Direction.NORTH, BlockStateModelGenerator.ROTATE_X_270)
                .register(BlockFace.WALL, Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90)
                .register(BlockFace.WALL, Direction.WEST, BlockStateModelGenerator.ROTATE_Y_270)
                .register(BlockFace.WALL, Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_180)
                .register(BlockFace.WALL, Direction.NORTH, BlockStateModelGenerator.NO_OP)
                .register(BlockFace.CEILING, Direction.EAST, BlockStateModelGenerator.ROTATE_Y_90.then(BlockStateModelGenerator.ROTATE_X_90))
                .register(BlockFace.CEILING, Direction.WEST, BlockStateModelGenerator.ROTATE_Y_270.then(BlockStateModelGenerator.ROTATE_X_90))
                .register(BlockFace.CEILING, Direction.SOUTH, BlockStateModelGenerator.ROTATE_Y_180.then(BlockStateModelGenerator.ROTATE_X_90))
                .register(BlockFace.CEILING, Direction.NORTH, BlockStateModelGenerator.ROTATE_X_90);
    }
}
