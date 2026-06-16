package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.extensions.block.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.extensions.item.ExtensionItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

class ExtensionsModelProvider extends FabricModelProvider {
    private static final PropertyDispatch<VariantMutator> UP_DEFAULT_ROTATION_OPERATIONS = PropertyDispatch.modify(BlockStateProperties.FACING)
            .select(Direction.DOWN, BlockModelGenerators.X_ROT_180)
            .select(Direction.UP, BlockModelGenerators.NOP)
            .select(Direction.NORTH, BlockModelGenerators.X_ROT_90)
            .select(Direction.SOUTH, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_180))
            .select(Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
            .select(Direction.EAST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90));

    public ExtensionsModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {
        registerDrawerBarrel(generators);
        generators.createNonTemplateModelBlock(ExtensionBlocks.ENDER_CONNECTOR);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generators.generateFlatItem(ExtensionItems.ENDER_CONNECTOR_LINKER, ModelTemplates.FLAT_HANDHELD_ITEM);
    }

    private void registerDrawerBarrel(BlockModelGenerators generators) {
        var block = ExtensionBlocks.DRAWER_BARREL;

        var topId = TextureMapping.getBlockTexture(Blocks.BARREL, "_top");
        var topOpenId = TextureMapping.getBlockTexture(Blocks.BARREL, "_top_open");
        var sideId = new Material(ExtendedDrawers.id("block/drawer_side"));
        var bottomId = new Material(ExtendedDrawers.id("block/drawer_base"));

        var closedVariant = BlockModelGenerators.plainVariant(
                TexturedModel.CUBE_TOP_BOTTOM
                        .get(block)
                        .updateTextures(textureMap ->
                                textureMap.put(TextureSlot.SIDE, sideId)
                                        .put(TextureSlot.BOTTOM, bottomId)
                                        .put(TextureSlot.TOP, topId))
                        .create(block, generators.modelOutput)
        );

        var openVariant = BlockModelGenerators.plainVariant(
                TexturedModel.CUBE_TOP_BOTTOM
                        .get(block)
                        .updateTextures(textureMap ->
                                textureMap.put(TextureSlot.SIDE, sideId)
                                        .put(TextureSlot.BOTTOM, bottomId)
                                        .put(TextureSlot.TOP, topOpenId))
                        .createWithSuffix(block, "_open", generators.modelOutput)
        );

        generators.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(block)
                        .with(PropertyDispatch.initial(BlockStateProperties.OPEN).select(false, closedVariant).select(true, openVariant))
                        .with(UP_DEFAULT_ROTATION_OPERATIONS)
        );
    }
}
