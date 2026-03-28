package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

class DrawersModelProvider extends FabricModelProvider {
    public DrawersModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        generators.createTrivialCube(ModBlocks.ACCESS_POINT);
        generators.createTrivialBlock(ModBlocks.CONNECTOR, _ -> TexturedModel.createAllSame(new Material(id("block/drawer_base"))));

        registerDrawerModel(ModBlocks.SINGLE_DRAWER, generators);
        registerDrawerModel(ModBlocks.DOUBLE_DRAWER, generators);
        registerDrawerModel(ModBlocks.QUAD_DRAWER, generators);

        generateShadowDrawerModel(generators);
        generateCompactingDrawerModel(generators);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generators.generateFlatItem(ModItems.T1_UPGRADE, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.T2_UPGRADE, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.T3_UPGRADE, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.T4_UPGRADE, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.CREATIVE_UPGRADE, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.LOCK, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.UPGRADE_FRAME, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.LIMITER, ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.DUPE_WAND, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private static void generateShadowDrawerModel(BlockModelGenerators generator) {
        var modelId = ModelTemplates.CUBE_ORIENTABLE.create(
                ModBlocks.SHADOW_DRAWER,
                TextureMapping.column(
                        new Material(id("block/shadow_drawer_side")), new Material(id("block/shadow_drawer_side"))
                        ).copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture(ModBlocks.SHADOW_DRAWER)),
                generator.modelOutput);
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

        var model = template.create(block, TextureMapping.singleSlot(TextureSlot.FRONT, TextureMapping.getBlockTexture(block)), generator.modelOutput);
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
