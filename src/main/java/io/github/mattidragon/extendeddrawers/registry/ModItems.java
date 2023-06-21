package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.item.DrawerItem;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModItems {
    public static final Item SINGLE_DRAWER = new DrawerItem(ModBlocks.SINGLE_DRAWER, new FabricItemSettings());
    public static final Item DOUBLE_DRAWER = new DrawerItem(ModBlocks.DOUBLE_DRAWER, new FabricItemSettings());
    public static final Item QUAD_DRAWER = new DrawerItem(ModBlocks.QUAD_DRAWER, new FabricItemSettings());
    public static final Item CONNECTOR = new BlockItem(ModBlocks.CONNECTOR, new FabricItemSettings());
    public static final Item SHADOW_DRAWER = new DrawerItem(ModBlocks.SHADOW_DRAWER, new FabricItemSettings());
    public static final Item COMPACTING_DRAWER = new DrawerItem(ModBlocks.COMPACTING_DRAWER, new FabricItemSettings());
    public static final Item ACCESS_POINT = new BlockItem(ModBlocks.ACCESS_POINT, new FabricItemSettings());
    
    public static final Item UPGRADE_FRAME = new Item(new FabricItemSettings());
    public static final UpgradeItem T1_UPGRADE = new UpgradeItem(new FabricItemSettings(), id("item/t1_upgrade"), 1);
    public static final UpgradeItem T2_UPGRADE = new UpgradeItem(new FabricItemSettings(), id("item/t2_upgrade"), 2);
    public static final UpgradeItem T3_UPGRADE = new UpgradeItem(new FabricItemSettings(), id("item/t3_upgrade"), 3);
    public static final UpgradeItem T4_UPGRADE = new UpgradeItem(new FabricItemSettings(), id("item/t4_upgrade"), 4);
    public static final UpgradeItem DOWNGRADE = new UpgradeItem(new FabricItemSettings(), id("item/downgrade"), value -> 64);
    public static final UpgradeItem CREATIVE_UPGRADE = new UpgradeItem(new FabricItemSettings(), id("item/creative_upgrade"), value -> Long.MAX_VALUE);
    
    public static final Item LOCK = new Item(new FabricItemSettings());
    
    public static void register() {
        Registry.register(Registries.ITEM, id("single_drawer"), SINGLE_DRAWER);
        Registry.register(Registries.ITEM, id("double_drawer"), DOUBLE_DRAWER);
        Registry.register(Registries.ITEM, id("quad_drawer"), QUAD_DRAWER);
        Registry.register(Registries.ITEM, id("connector"), CONNECTOR);
        Registry.register(Registries.ITEM, id("shadow_drawer"), SHADOW_DRAWER);
        Registry.register(Registries.ITEM, id("compacting_drawer"), COMPACTING_DRAWER);
        Registry.register(Registries.ITEM, id("access_point"), ACCESS_POINT);
        
        Registry.register(Registries.ITEM, id("upgrade_frame"), UPGRADE_FRAME);
        Registry.register(Registries.ITEM, id("t1_upgrade"), T1_UPGRADE);
        Registry.register(Registries.ITEM, id("t2_upgrade"), T2_UPGRADE);
        Registry.register(Registries.ITEM, id("t3_upgrade"), T3_UPGRADE);
        Registry.register(Registries.ITEM, id("t4_upgrade"), T4_UPGRADE);
        Registry.register(Registries.ITEM, id("downgrade"), DOWNGRADE);
        Registry.register(Registries.ITEM, id("creative_upgrade"), CREATIVE_UPGRADE);
        
        Registry.register(Registries.ITEM, id("lock"), LOCK);
    }
}
