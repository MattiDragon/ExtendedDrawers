package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.data.client.*;
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
        generator.register(ModItems.CREATIVE_UPGRADE, Models.GENERATED);
        generator.register(ModItems.LOCK, Models.GENERATED);
        generator.register(ModItems.UPGRADE_FRAME, Models.GENERATED);
        generator.register(ModItems.LIMITER, Models.GENERATED);
        generator.register(ModItems.DUPE_WAND, Models.GENERATED);
    }

    private void generateCompactingDrawerModel(BlockStateModelGenerator generator) {
        generator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(
                                ModBlocks.COMPACTING_DRAWER,
                                BlockStateVariant.create().put(VariantSettings.MODEL, id("block/compacting_drawer")))
                        .coordinate(getBlockStateMap()));
    }

    private void registerDrawerModel(Block block, BlockStateModelGenerator generator) {
        var template = new Model(Optional.of(id("drawer_template")), Optional.empty(), TextureKey.FRONT);

        var model = template.upload(block, TextureMap.of(TextureKey.FRONT, ModelIds.getBlockModelId(block)), generator.modelCollector);
        generator.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model)).coordinate(getBlockStateMap()));
    }

    private static BlockStateVariantMap.DoubleProperty<WallMountLocation, Direction> getBlockStateMap() {
        return BlockStateVariantMap.create(Properties.WALL_MOUNT_LOCATION, Properties.HORIZONTAL_FACING)
                .register(WallMountLocation.FLOOR, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R270))
                .register(WallMountLocation.FLOOR, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R270))
                .register(WallMountLocation.FLOOR, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R270))
                .register(WallMountLocation.FLOOR, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R0).put(VariantSettings.X, VariantSettings.Rotation.R270))
                .register(WallMountLocation.WALL, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
                .register(WallMountLocation.WALL, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
                .register(WallMountLocation.WALL, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
                .register(WallMountLocation.WALL, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R0))
                .register(WallMountLocation.CEILING, Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90).put(VariantSettings.X, VariantSettings.Rotation.R90))
                .register(WallMountLocation.CEILING, Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270).put(VariantSettings.X, VariantSettings.Rotation.R90))
                .register(WallMountLocation.CEILING, Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180).put(VariantSettings.X, VariantSettings.Rotation.R90))
                .register(WallMountLocation.CEILING, Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R0).put(VariantSettings.X, VariantSettings.Rotation.R90));
    }
}
