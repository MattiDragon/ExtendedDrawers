package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.block.ConnectorBlock;
import io.github.mattidragon.extendeddrawers.block.ControllerBlock;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.block.ShadowDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModBlocks {
    public static final DrawerBlock SINGLE_DRAWER = new DrawerBlock(FabricBlockSettings.of(Material.WOOD).strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 1);
    public static final DrawerBlock DOUBLE_DRAWER = new DrawerBlock(FabricBlockSettings.of(Material.WOOD).strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 2);
    public static final DrawerBlock QUAD_DRAWER = new DrawerBlock(FabricBlockSettings.of(Material.WOOD).strength(2f, 3f).sounds(BlockSoundGroup.WOOD), 4);
    public static final ConnectorBlock CONNECTOR = new ConnectorBlock(FabricBlockSettings.of(Material.WOOD).strength(2f, 3f).sounds(BlockSoundGroup.WOOD));
    public static final ControllerBlock CONTROLLER = new ControllerBlock(FabricBlockSettings.of(Material.STONE).strength(3f, 9f).sounds(BlockSoundGroup.STONE));
    public static final ShadowDrawerBlock SHADOW_DRAWER = new ShadowDrawerBlock(FabricBlockSettings.of(Material.STONE, MapColor.PALE_YELLOW).strength(3f, 9f).sounds(BlockSoundGroup.STONE));
    
    public static final BlockEntityType<DrawerBlockEntity> DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(DrawerBlockEntity::new, SINGLE_DRAWER, DOUBLE_DRAWER, QUAD_DRAWER).build();
    public static final BlockEntityType<ShadowDrawerBlockEntity> SHADOW_DRAWER_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ShadowDrawerBlockEntity::new, SHADOW_DRAWER).build();
    
    public static void register() {
        Registry.register(Registry.BLOCK, id("single_drawer"), SINGLE_DRAWER);
        Registry.register(Registry.BLOCK, id("double_drawer"), DOUBLE_DRAWER);
        Registry.register(Registry.BLOCK, id("quad_drawer"), QUAD_DRAWER);
        Registry.register(Registry.BLOCK, id("connector"), CONNECTOR);
        Registry.register(Registry.BLOCK, id("controller"), CONTROLLER);
        Registry.register(Registry.BLOCK, id("shadow_drawer"), SHADOW_DRAWER);
        
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("drawer"), DRAWER_BLOCK_ENTITY);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id("shadow_drawer"), SHADOW_DRAWER_BLOCK_ENTITY);
    }
}
