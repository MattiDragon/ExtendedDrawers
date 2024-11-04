package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.item.DrawerItem;
import io.github.mattidragon.extendeddrawers.item.LimiterItem;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModItems {
    public static final Item SINGLE_DRAWER = new DrawerItem(ModBlocks.SINGLE_DRAWER, new Item.Settings().registryKey(key("single_drawer")).useBlockPrefixedTranslationKey());
    public static final Item DOUBLE_DRAWER = new DrawerItem(ModBlocks.DOUBLE_DRAWER, new Item.Settings().registryKey(key("double_drawer")).useBlockPrefixedTranslationKey());
    public static final Item QUAD_DRAWER = new DrawerItem(ModBlocks.QUAD_DRAWER, new Item.Settings().registryKey(key("quad_drawer")).useBlockPrefixedTranslationKey());
    public static final Item CONNECTOR = new BlockItem(ModBlocks.CONNECTOR, new Item.Settings().registryKey(key("connector")).useBlockPrefixedTranslationKey());
    public static final Item SHADOW_DRAWER = new DrawerItem(ModBlocks.SHADOW_DRAWER, new Item.Settings().registryKey(key("shadow_drawer")).useBlockPrefixedTranslationKey());
    public static final Item COMPACTING_DRAWER = new DrawerItem(ModBlocks.COMPACTING_DRAWER, new Item.Settings().registryKey(key("compacting_drawer")).useBlockPrefixedTranslationKey());
    public static final Item ACCESS_POINT = new BlockItem(ModBlocks.ACCESS_POINT, new Item.Settings().registryKey(key("access_point")).useBlockPrefixedTranslationKey());
    
    public static final Item UPGRADE_FRAME = new Item(new Item.Settings().registryKey(key("upgrade_frame")));
    public static final UpgradeItem T1_UPGRADE = new UpgradeItem(new Item.Settings().registryKey(key("t1_upgrade")), id("item/t1_upgrade"), 1);
    public static final UpgradeItem T2_UPGRADE = new UpgradeItem(new Item.Settings().registryKey(key("t2_upgrade")), id("item/t2_upgrade"), 2);
    public static final UpgradeItem T3_UPGRADE = new UpgradeItem(new Item.Settings().registryKey(key("t3_upgrade")), id("item/t3_upgrade"), 3);
    public static final UpgradeItem T4_UPGRADE = new UpgradeItem(new Item.Settings().registryKey(key("t4_upgrade")), id("item/t4_upgrade"), 4);
    public static final UpgradeItem CREATIVE_UPGRADE = new UpgradeItem(new Item.Settings().registryKey(key("creative_upgrade")), id("item/creative_upgrade"), value -> Long.MAX_VALUE);
    public static final LimiterItem LIMITER = new LimiterItem(new Item.Settings().registryKey(key("limiter")));
    public static final Item LOCK = new Item(new Item.Settings().registryKey(key("lock")));
    public static final Item DUPE_WAND = new Item(new Item.Settings().registryKey(key("dupe_wand")));

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
        Registry.register(Registries.ITEM, id("creative_upgrade"), CREATIVE_UPGRADE);
        Registry.register(Registries.ITEM, id("limiter"), LIMITER);
        Registry.register(Registries.ITEM, id("lock"), LOCK);
        Registry.register(Registries.ITEM, id("dupe_wand"), DUPE_WAND);
    }

    private static RegistryKey<Item> key(String path) {
        return RegistryKey.of(RegistryKeys.ITEM, id(path));
    }
}
