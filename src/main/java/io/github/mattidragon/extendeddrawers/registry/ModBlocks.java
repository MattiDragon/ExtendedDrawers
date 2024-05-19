package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.block.*;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModBlocks {
    public static final DrawerBlock SINGLE_DRAWER = new DrawerBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 1);
    public static final DrawerBlock DOUBLE_DRAWER = new DrawerBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 2);
    public static final DrawerBlock QUAD_DRAWER = new DrawerBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 4);
    public static final ConnectorBlock CONNECTOR = new ConnectorBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).burnable().strength(2f, 3f).sounds(BlockSoundGroup.WOOD));
    public static final AccessPointBlock ACCESS_POINT = new AccessPointBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(3f, 9f).sounds(BlockSoundGroup.STONE));
    public static final ShadowDrawerBlock SHADOW_DRAWER = new ShadowDrawerBlock(AbstractBlock.Settings.create().mapColor(MapColor.PALE_YELLOW).strength(3f, 9f).sounds(BlockSoundGroup.STONE));
    public static final CompactingDrawerBlock COMPACTING_DRAWER = new CompactingDrawerBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(3f, 9f).sounds(BlockSoundGroup.STONE));

    public static final BlockEntityType<DrawerBlockEntity> DRAWER_BLOCK_ENTITY = BlockEntityType.Builder.create(DrawerBlockEntity::new, SINGLE_DRAWER, DOUBLE_DRAWER, QUAD_DRAWER).build();
    public static final BlockEntityType<CompactingDrawerBlockEntity> COMPACTING_DRAWER_BLOCK_ENTITY = BlockEntityType.Builder.create(CompactingDrawerBlockEntity::new, COMPACTING_DRAWER).build();
    public static final BlockEntityType<ShadowDrawerBlockEntity> SHADOW_DRAWER_BLOCK_ENTITY = BlockEntityType.Builder.create(ShadowDrawerBlockEntity::new, SHADOW_DRAWER).build();
    
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
}
