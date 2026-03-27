package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.item.DrawerItem;
import io.github.mattidragon.extendeddrawers.item.LimiterItem;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModItems {
    public static final Item SINGLE_DRAWER = new DrawerItem(ModBlocks.SINGLE_DRAWER, new Item.Properties().setId(key("single_drawer")).useBlockDescriptionPrefix());
    public static final Item DOUBLE_DRAWER = new DrawerItem(ModBlocks.DOUBLE_DRAWER, new Item.Properties().setId(key("double_drawer")).useBlockDescriptionPrefix());
    public static final Item QUAD_DRAWER = new DrawerItem(ModBlocks.QUAD_DRAWER, new Item.Properties().setId(key("quad_drawer")).useBlockDescriptionPrefix());
    public static final Item CONNECTOR = new BlockItem(ModBlocks.CONNECTOR, new Item.Properties().setId(key("connector")).useBlockDescriptionPrefix());
    public static final Item SHADOW_DRAWER = new DrawerItem(ModBlocks.SHADOW_DRAWER, new Item.Properties().setId(key("shadow_drawer")).useBlockDescriptionPrefix());
    public static final Item COMPACTING_DRAWER = new DrawerItem(ModBlocks.COMPACTING_DRAWER, new Item.Properties().setId(key("compacting_drawer")).useBlockDescriptionPrefix());
    public static final Item ACCESS_POINT = new BlockItem(ModBlocks.ACCESS_POINT, new Item.Properties().setId(key("access_point")).useBlockDescriptionPrefix());
    
    public static final Item UPGRADE_FRAME = new Item(new Item.Properties().setId(key("upgrade_frame")));
    public static final UpgradeItem T1_UPGRADE = new UpgradeItem(new Item.Properties().setId(key("t1_upgrade")), id("item/t1_upgrade"), 1);
    public static final UpgradeItem T2_UPGRADE = new UpgradeItem(new Item.Properties().setId(key("t2_upgrade")), id("item/t2_upgrade"), 2);
    public static final UpgradeItem T3_UPGRADE = new UpgradeItem(new Item.Properties().setId(key("t3_upgrade")), id("item/t3_upgrade"), 3);
    public static final UpgradeItem T4_UPGRADE = new UpgradeItem(new Item.Properties().setId(key("t4_upgrade")), id("item/t4_upgrade"), 4);
    public static final UpgradeItem CREATIVE_UPGRADE = new UpgradeItem(new Item.Properties().setId(key("creative_upgrade")), id("item/creative_upgrade"), value -> Long.MAX_VALUE);
    public static final LimiterItem LIMITER = new LimiterItem(new Item.Properties().setId(key("limiter")));
    public static final Item LOCK = new Item(new Item.Properties().setId(key("lock")));
    public static final Item DUPE_WAND = new Item(new Item.Properties().setId(key("dupe_wand")));

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, id("single_drawer"), SINGLE_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, id("double_drawer"), DOUBLE_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, id("quad_drawer"), QUAD_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, id("connector"), CONNECTOR);
        Registry.register(BuiltInRegistries.ITEM, id("shadow_drawer"), SHADOW_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, id("compacting_drawer"), COMPACTING_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, id("access_point"), ACCESS_POINT);
        
        Registry.register(BuiltInRegistries.ITEM, id("upgrade_frame"), UPGRADE_FRAME);
        Registry.register(BuiltInRegistries.ITEM, id("t1_upgrade"), T1_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, id("t2_upgrade"), T2_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, id("t3_upgrade"), T3_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, id("t4_upgrade"), T4_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, id("creative_upgrade"), CREATIVE_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, id("limiter"), LIMITER);
        Registry.register(BuiltInRegistries.ITEM, id("lock"), LOCK);
        Registry.register(BuiltInRegistries.ITEM, id("dupe_wand"), DUPE_WAND);
    }

    private static ResourceKey<Item> key(String path) {
        return ResourceKey.create(Registries.ITEM, id(path));
    }
}
