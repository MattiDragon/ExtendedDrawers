package io.github.mattidragon.extendeddrawers.extensions.registry;

import io.github.mattidragon.extendeddrawers.extensions.block.DrawerBarrelBlock;
import io.github.mattidragon.extendeddrawers.extensions.block.EnderConnectorBlock;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.DrawerBarrelBlockEntity;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.EnderConnectorBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class ExtensionBlocks {
    public static final Block DRAWER_BARREL = new DrawerBarrelBlock(BlockBehaviour.Properties.of().setId(key("drawer_barrel")).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD));
    public static final Block ENDER_CONNECTOR = new EnderConnectorBlock(BlockBehaviour.Properties.of().setId(key("ender_connector")).mapColor(MapColor.SAND).strength(3f, 9f).sound(SoundType.STONE));

    public static final BlockEntityType<DrawerBarrelBlockEntity> DRAWER_BARREL_ENTITY = FabricBlockEntityTypeBuilder.create(DrawerBarrelBlockEntity::new, DRAWER_BARREL).build();
    public static final BlockEntityType<EnderConnectorBlockEntity> ENDER_CONNECTOR_ENTITY = FabricBlockEntityTypeBuilder.create(EnderConnectorBlockEntity::new, ENDER_CONNECTOR).build();

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, id("drawer_barrel"), DRAWER_BARREL);
        Registry.register(BuiltInRegistries.BLOCK, id("ender_connector"), ENDER_CONNECTOR);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("drawer_barrel"), DRAWER_BARREL_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("ender_connector"), ENDER_CONNECTOR_ENTITY);
    }

    private static ResourceKey<Block> key(String path) {
        return ResourceKey.create(Registries.BLOCK, id(path));
    }
}
