package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.item.DrawerItem;
import io.github.mattidragon.extendeddrawers.item.LockItem;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.MOD_GROUP;
import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModItems {
    public static final Item SINGLE_DRAWER = new DrawerItem(ModBlocks.SINGLE_DRAWER, new FabricItemSettings().group(MOD_GROUP));
    public static final Item DOUBLE_DRAWER = new DrawerItem(ModBlocks.DOUBLE_DRAWER, new FabricItemSettings().group(MOD_GROUP));
    public static final Item QUAD_DRAWER = new DrawerItem(ModBlocks.QUAD_DRAWER, new FabricItemSettings().group(MOD_GROUP));
    public static final Item CONNECTOR = new BlockItem(ModBlocks.CONNECTOR, new FabricItemSettings().group(MOD_GROUP));
    public static final Item SHADOW_DRAWER = new DrawerItem(ModBlocks.SHADOW_DRAWER, new FabricItemSettings().group(MOD_GROUP));
    public static final Item CONTROLLER = new BlockItem(ModBlocks.CONTROLLER, new FabricItemSettings().group(MOD_GROUP));
    
    public static final Item UPGRADE_FRAME = new Item(new FabricItemSettings().group(MOD_GROUP));
    public static final UpgradeItem T1_UPGRADE = new UpgradeItem(new FabricItemSettings().group(MOD_GROUP), id("item/t1_upgrade"), 2);
    public static final UpgradeItem T2_UPGRADE = new UpgradeItem(new FabricItemSettings().group(MOD_GROUP), id("item/t2_upgrade"), 4);
    public static final UpgradeItem T3_UPGRADE = new UpgradeItem(new FabricItemSettings().group(MOD_GROUP), id("item/t3_upgrade"), 8);
    public static final UpgradeItem T4_UPGRADE = new UpgradeItem(new FabricItemSettings().group(MOD_GROUP), id("item/t4_upgrade"), 16);
    public static final UpgradeItem DOWNGRADE = new UpgradeItem(new FabricItemSettings().group(MOD_GROUP), id("item/downgrade"), value -> 64);
    public static final UpgradeItem CREATIVE_UPGRADE = new UpgradeItem(new FabricItemSettings().group(MOD_GROUP), id("item/creative_upgrade"), value -> Long.MAX_VALUE);
    
    public static final Item LOCK = new LockItem(new FabricItemSettings().group(MOD_GROUP));
    
    public static void register() {
        Registry.register(Registry.ITEM, id("single_drawer"), SINGLE_DRAWER);
        Registry.register(Registry.ITEM, id("double_drawer"), DOUBLE_DRAWER);
        Registry.register(Registry.ITEM, id("quad_drawer"), QUAD_DRAWER);
        Registry.register(Registry.ITEM, id("connector"), CONNECTOR);
        Registry.register(Registry.ITEM, id("shadow_drawer"), SHADOW_DRAWER);
        Registry.register(Registry.ITEM, id("controller"), CONTROLLER);
        
        Registry.register(Registry.ITEM, id("upgrade_frame"), UPGRADE_FRAME);
        Registry.register(Registry.ITEM, id("t1_upgrade"), T1_UPGRADE);
        Registry.register(Registry.ITEM, id("t2_upgrade"), T2_UPGRADE);
        Registry.register(Registry.ITEM, id("t3_upgrade"), T3_UPGRADE);
        Registry.register(Registry.ITEM, id("t4_upgrade"), T4_UPGRADE);
        Registry.register(Registry.ITEM, id("downgrade"), DOWNGRADE);
        Registry.register(Registry.ITEM, id("creative_upgrade"), CREATIVE_UPGRADE);
        
        Registry.register(Registry.ITEM, id("lock"), LOCK);
    }
}
