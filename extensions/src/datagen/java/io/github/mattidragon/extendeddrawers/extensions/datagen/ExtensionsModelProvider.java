package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Blocks;
import net.minecraft.client.data.*;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

class ExtensionsModelProvider extends FabricModelProvider {
    private static final BlockStateVariantMap<ModelVariantOperator> UP_DEFAULT_ROTATION_OPERATIONS = BlockStateVariantMap.operations(Properties.FACING)
            .register(Direction.DOWN, BlockStateModelGenerator.ROTATE_X_180)
            .register(Direction.UP, BlockStateModelGenerator.NO_OP)
            .register(Direction.NORTH, BlockStateModelGenerator.ROTATE_X_90)
            .register(Direction.SOUTH, BlockStateModelGenerator.ROTATE_X_90.then(BlockStateModelGenerator.ROTATE_Y_180))
            .register(Direction.WEST, BlockStateModelGenerator.ROTATE_X_90.then(BlockStateModelGenerator.ROTATE_Y_270))
            .register(Direction.EAST, BlockStateModelGenerator.ROTATE_X_90.then(BlockStateModelGenerator.ROTATE_Y_90));

    public ExtensionsModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        registerDrawerBarrel(generator);
        generator.registerSimpleState(ExtensionBlocks.ENDER_CONNECTOR);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(ExtensionItems.ENDER_CONNECTOR_LINKER, Models.HANDHELD);
    }

    private void registerDrawerBarrel(BlockStateModelGenerator generator) {
        var block = ExtensionBlocks.DRAWER_BARREL;

        var topId = TextureMap.getSubId(Blocks.BARREL, "_top");
        var topOpenId = TextureMap.getSubId(Blocks.BARREL, "_top_open");
        var sideId = ExtendedDrawers.id("block/drawer_side");
        var bottomId = ExtendedDrawers.id("block/drawer_base");

        var closedVariant = BlockStateModelGenerator.createWeightedVariant(
                TexturedModel.CUBE_BOTTOM_TOP
                        .get(block)
                        .textures(textureMap ->
                                textureMap.put(TextureKey.SIDE, sideId)
                                        .put(TextureKey.BOTTOM, bottomId)
                                        .put(TextureKey.TOP, topId))
                        .upload(block, generator.modelCollector)
        );

        var openVariant = BlockStateModelGenerator.createWeightedVariant(
                TexturedModel.CUBE_BOTTOM_TOP
                        .get(block)
                        .textures(textureMap ->
                                textureMap.put(TextureKey.SIDE, sideId)
                                        .put(TextureKey.BOTTOM, bottomId)
                                        .put(TextureKey.TOP, topOpenId))
                        .upload(block, "_open", generator.modelCollector)
        );

        generator.blockStateCollector.accept(
                VariantsBlockModelDefinitionCreator.of(block)
                        .with(BlockStateVariantMap.models(Properties.OPEN).register(false, closedVariant).register(true, openVariant))
                        .coordinate(UP_DEFAULT_ROTATION_OPERATIONS)
        );
    }
}
