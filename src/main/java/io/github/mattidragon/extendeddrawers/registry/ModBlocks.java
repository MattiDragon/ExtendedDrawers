package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.block.*;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
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

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModBlocks {
    public static final DrawerBlock SINGLE_DRAWER = new DrawerBlock(BlockBehaviour.Properties.of().setId(key("single_drawer")).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD), 1);
    public static final DrawerBlock DOUBLE_DRAWER = new DrawerBlock(BlockBehaviour.Properties.of().setId(key("double_drawer")).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD), 2);
    public static final DrawerBlock QUAD_DRAWER = new DrawerBlock(BlockBehaviour.Properties.of().setId(key("quad_drawer")).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD), 4);
    public static final ConnectorBlock CONNECTOR = new ConnectorBlock(BlockBehaviour.Properties.of().setId(key("connector")).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD));
    public static final AccessPointBlock ACCESS_POINT = new AccessPointBlock(BlockBehaviour.Properties.of().setId(key("access_point")).mapColor(MapColor.STONE).strength(3f, 9f).sound(SoundType.STONE));
    public static final ShadowDrawerBlock SHADOW_DRAWER = new ShadowDrawerBlock(BlockBehaviour.Properties.of().setId(key("shadow_drawer")).mapColor(MapColor.SAND).strength(3f, 9f).sound(SoundType.STONE));
    public static final CompactingDrawerBlock COMPACTING_DRAWER = new CompactingDrawerBlock(BlockBehaviour.Properties.of().setId(key("compacting_drawer")).mapColor(MapColor.STONE).strength(3f, 9f).sound(SoundType.STONE));

    public static final BlockEntityType<DrawerBlockEntity> DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(DrawerBlockEntity::new, SINGLE_DRAWER, DOUBLE_DRAWER, QUAD_DRAWER).build();
    public static final BlockEntityType<CompactingDrawerBlockEntity> COMPACTING_DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(CompactingDrawerBlockEntity::new, COMPACTING_DRAWER).build();
    public static final BlockEntityType<ShadowDrawerBlockEntity> SHADOW_DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ShadowDrawerBlockEntity::new, SHADOW_DRAWER).build();
    
    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, id("single_drawer"), SINGLE_DRAWER);
        Registry.register(BuiltInRegistries.BLOCK, id("double_drawer"), DOUBLE_DRAWER);
        Registry.register(BuiltInRegistries.BLOCK, id("quad_drawer"), QUAD_DRAWER);
        Registry.register(BuiltInRegistries.BLOCK, id("connector"), CONNECTOR);
        Registry.register(BuiltInRegistries.BLOCK, id("access_point"), ACCESS_POINT);
        Registry.register(BuiltInRegistries.BLOCK, id("shadow_drawer"), SHADOW_DRAWER);
        Registry.register(BuiltInRegistries.BLOCK, id("compacting_drawer"), COMPACTING_DRAWER);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("drawer"), DRAWER_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("compacting_drawer"), COMPACTING_DRAWER_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("shadow_drawer"), SHADOW_DRAWER_BLOCK_ENTITY);
    }
    
    private static ResourceKey<Block> key(String path) {
        return ResourceKey.create(Registries.BLOCK, id(path));
    } 
}
