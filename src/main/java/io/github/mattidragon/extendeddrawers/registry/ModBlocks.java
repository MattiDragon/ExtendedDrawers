package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.block.*;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModBlocks {
    public static final DrawerBlock SINGLE_DRAWER = new DrawerBlock(AbstractBlock.Settings.create().registryKey(key("single_drawer")).mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 1);
    public static final DrawerBlock DOUBLE_DRAWER = new DrawerBlock(AbstractBlock.Settings.create().registryKey(key("double_drawer")).mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 2);
    public static final DrawerBlock QUAD_DRAWER = new DrawerBlock(AbstractBlock.Settings.create().registryKey(key("quad_drawer")).mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 4);
    public static final ConnectorBlock CONNECTOR = new ConnectorBlock(AbstractBlock.Settings.create().registryKey(key("connector")).mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD));
    public static final AccessPointBlock ACCESS_POINT = new AccessPointBlock(AbstractBlock.Settings.create().registryKey(key("access_point")).mapColor(MapColor.STONE_GRAY).strength(3f, 9f).sounds(BlockSoundGroup.STONE));
    public static final ShadowDrawerBlock SHADOW_DRAWER = new ShadowDrawerBlock(AbstractBlock.Settings.create().registryKey(key("shadow_drawer")).mapColor(MapColor.PALE_YELLOW).strength(3f, 9f).sounds(BlockSoundGroup.STONE));
    public static final CompactingDrawerBlock COMPACTING_DRAWER = new CompactingDrawerBlock(AbstractBlock.Settings.create().registryKey(key("compacting_drawer")).mapColor(MapColor.STONE_GRAY).strength(3f, 9f).sounds(BlockSoundGroup.STONE));

    public static final BlockEntityType<DrawerBlockEntity> DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(DrawerBlockEntity::new, SINGLE_DRAWER, DOUBLE_DRAWER, QUAD_DRAWER).build();
    public static final BlockEntityType<CompactingDrawerBlockEntity> COMPACTING_DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(CompactingDrawerBlockEntity::new, COMPACTING_DRAWER).build();
    public static final BlockEntityType<ShadowDrawerBlockEntity> SHADOW_DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ShadowDrawerBlockEntity::new, SHADOW_DRAWER).build();
    
    public static void register() {
        Registry.register(Registries.BLOCK, id("single_drawer"), SINGLE_DRAWER);
        Registry.register(Registries.BLOCK, id("double_drawer"), DOUBLE_DRAWER);
        Registry.register(Registries.BLOCK, id("quad_drawer"), QUAD_DRAWER);
        Registry.register(Registries.BLOCK, id("connector"), CONNECTOR);
        Registry.register(Registries.BLOCK, id("access_point"), ACCESS_POINT);
        Registry.register(Registries.BLOCK, id("shadow_drawer"), SHADOW_DRAWER);
        Registry.register(Registries.BLOCK, id("compacting_drawer"), COMPACTING_DRAWER);

        Registry.register(Registries.BLOCK_ENTITY_TYPE, id("drawer"), DRAWER_BLOCK_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id("compacting_drawer"), COMPACTING_DRAWER_BLOCK_ENTITY);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, id("shadow_drawer"), SHADOW_DRAWER_BLOCK_ENTITY);
    }
    
    private static RegistryKey<Block> key(String path) {
        return RegistryKey.of(RegistryKeys.BLOCK, id(path));
    } 
}
