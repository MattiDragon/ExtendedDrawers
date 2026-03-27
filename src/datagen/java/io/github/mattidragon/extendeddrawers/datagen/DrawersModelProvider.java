package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

class DrawersModelProvider extends FabricModelProvider {
    public DrawersModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        generator.createTrivialCube(ModBlocks.ACCESS_POINT);
        generator.createTrivialBlock(ModBlocks.CONNECTOR, block -> TexturedModel.createAllSame(id("block/drawer_base")));

        registerDrawerModel(ModBlocks.SINGLE_DRAWER, generator);
        registerDrawerModel(ModBlocks.DOUBLE_DRAWER, generator);
        registerDrawerModel(ModBlocks.QUAD_DRAWER, generator);

        generateShadowDrawerModel(generator);
        generateCompactingDrawerModel(generator);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        generator.generateFlatItem(ModItems.T1_UPGRADE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.T2_UPGRADE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.T3_UPGRADE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.T4_UPGRADE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.CREATIVE_UPGRADE, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.LOCK, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.UPGRADE_FRAME, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.LIMITER, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(ModItems.DUPE_WAND, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private static void generateShadowDrawerModel(BlockModelGenerators generator) {
        var modelId = ModelTemplates.CUBE_ORIENTABLE.create(ModBlocks.SHADOW_DRAWER, TextureMapping.column(id("block/shadow_drawer_side"), id("block/shadow_drawer_side")).copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(ModBlocks.SHADOW_DRAWER)), generator.modelOutput);
        generator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                                ModBlocks.SHADOW_DRAWER,
                                BlockModelGenerators.plainVariant(modelId))
                        .with(getBlockStateMap()));
    }

    private void generateCompactingDrawerModel(BlockModelGenerators generator) {
        generator.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                                ModBlocks.COMPACTING_DRAWER,
                                BlockModelGenerators.plainVariant(id("block/compacting_drawer")))
                        .with(getBlockStateMap()));
    }

    private void registerDrawerModel(Block block, BlockModelGenerators generator) {
        var template = new ModelTemplate(Optional.of(id("drawer_template")), Optional.empty(), TextureSlot.FRONT);

        var model = template.create(block, TextureMapping.singleSlot(TextureSlot.FRONT, ModelLocationUtils.getModelLocation(block)), generator.modelOutput);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(model)).with(getBlockStateMap()));
    }

    private static PropertyDispatch<VariantMutator> getBlockStateMap() {
        return PropertyDispatch.modify(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                .select(AttachFace.FLOOR, Direction.EAST, BlockModelGenerators.Y_ROT_90.then(BlockModelGenerators.X_ROT_270))
                .select(AttachFace.FLOOR, Direction.WEST, BlockModelGenerators.Y_ROT_270.then(BlockModelGenerators.X_ROT_270))
                .select(AttachFace.FLOOR, Direction.SOUTH, BlockModelGenerators.Y_ROT_180.then(BlockModelGenerators.X_ROT_270))
                .select(AttachFace.FLOOR, Direction.NORTH, BlockModelGenerators.X_ROT_270)
                .select(AttachFace.WALL, Direction.EAST, BlockModelGenerators.Y_ROT_90)
                .select(AttachFace.WALL, Direction.WEST, BlockModelGenerators.Y_ROT_270)
                .select(AttachFace.WALL, Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
                .select(AttachFace.WALL, Direction.NORTH, BlockModelGenerators.NOP)
                .select(AttachFace.CEILING, Direction.EAST, BlockModelGenerators.Y_ROT_90.then(BlockModelGenerators.X_ROT_90))
                .select(AttachFace.CEILING, Direction.WEST, BlockModelGenerators.Y_ROT_270.then(BlockModelGenerators.X_ROT_90))
                .select(AttachFace.CEILING, Direction.SOUTH, BlockModelGenerators.Y_ROT_180.then(BlockModelGenerators.X_ROT_90))
                .select(AttachFace.CEILING, Direction.NORTH, BlockModelGenerators.X_ROT_90);
    }
}
