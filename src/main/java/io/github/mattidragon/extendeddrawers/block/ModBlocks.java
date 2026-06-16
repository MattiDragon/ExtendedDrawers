package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModBlocks {
    public static final DrawerBlock SINGLE_DRAWER = new DrawerBlock(BlockBehaviour.Properties.of().setId(ModBlockItemIds.SINGLE_DRAWER.block()).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD), 1);
    public static final DrawerBlock DOUBLE_DRAWER = new DrawerBlock(BlockBehaviour.Properties.of().setId(ModBlockItemIds.DOUBLE_DRAWER.block()).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD), 2);
    public static final DrawerBlock QUAD_DRAWER = new DrawerBlock(BlockBehaviour.Properties.of().setId(ModBlockItemIds.QUAD_DRAWER.block()).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD), 4);
    public static final ConnectorBlock CONNECTOR = new ConnectorBlock(BlockBehaviour.Properties.of().setId(ModBlockItemIds.CONNECTOR.block()).mapColor(MapColor.PODZOL).ignitedByLava().strength(2f, 3f).sound(SoundType.WOOD));
    public static final AccessPointBlock ACCESS_POINT = new AccessPointBlock(BlockBehaviour.Properties.of().setId(ModBlockItemIds.ACCESS_POINT.block()).mapColor(MapColor.STONE).strength(3f, 9f).sound(SoundType.STONE));
    public static final ShadowDrawerBlock SHADOW_DRAWER = new ShadowDrawerBlock(BlockBehaviour.Properties.of().setId(ModBlockItemIds.SHADOW_DRAWER.block()).mapColor(MapColor.SAND).strength(3f, 9f).sound(SoundType.STONE));
    public static final CompactingDrawerBlock COMPACTING_DRAWER = new CompactingDrawerBlock(BlockBehaviour.Properties.of().setId(ModBlockItemIds.COMPACTING_DRAWER.block()).mapColor(MapColor.STONE).strength(3f, 9f).sound(SoundType.STONE));

    public static final BlockEntityType<DrawerBlockEntity> DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(DrawerBlockEntity::new, SINGLE_DRAWER, DOUBLE_DRAWER, QUAD_DRAWER).build();
    public static final BlockEntityType<CompactingDrawerBlockEntity> COMPACTING_DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(CompactingDrawerBlockEntity::new, COMPACTING_DRAWER).build();
    public static final BlockEntityType<ShadowDrawerBlockEntity> SHADOW_DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ShadowDrawerBlockEntity::new, SHADOW_DRAWER).build();
    
    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, ModBlockItemIds.SINGLE_DRAWER.block(), SINGLE_DRAWER);
        Registry.register(BuiltInRegistries.BLOCK, ModBlockItemIds.DOUBLE_DRAWER.block(), DOUBLE_DRAWER);
        Registry.register(BuiltInRegistries.BLOCK, ModBlockItemIds.QUAD_DRAWER.block(), QUAD_DRAWER);
        Registry.register(BuiltInRegistries.BLOCK, ModBlockItemIds.CONNECTOR.block(), CONNECTOR);
        Registry.register(BuiltInRegistries.BLOCK, ModBlockItemIds.ACCESS_POINT.block(), ACCESS_POINT);
        Registry.register(BuiltInRegistries.BLOCK, ModBlockItemIds.SHADOW_DRAWER.block(), SHADOW_DRAWER);
        Registry.register(BuiltInRegistries.BLOCK, ModBlockItemIds.COMPACTING_DRAWER.block(), COMPACTING_DRAWER);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("drawer"), DRAWER_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("compacting_drawer"), COMPACTING_DRAWER_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("shadow_drawer"), SHADOW_DRAWER_BLOCK_ENTITY);
    }
}
