package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.block.ModBlockItemIds;
import io.github.mattidragon.extendeddrawers.block.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModItems {
    public static final Item SINGLE_DRAWER = new DrawerItem(ModBlocks.SINGLE_DRAWER, new Item.Properties().setId(ModBlockItemIds.SINGLE_DRAWER.item()).useBlockDescriptionPrefix());
    public static final Item DOUBLE_DRAWER = new DrawerItem(ModBlocks.DOUBLE_DRAWER, new Item.Properties().setId(ModBlockItemIds.DOUBLE_DRAWER.item()).useBlockDescriptionPrefix());
    public static final Item QUAD_DRAWER = new DrawerItem(ModBlocks.QUAD_DRAWER, new Item.Properties().setId(ModBlockItemIds.QUAD_DRAWER.item()).useBlockDescriptionPrefix());
    public static final Item CONNECTOR = new BlockItem(ModBlocks.CONNECTOR, new Item.Properties().setId(ModBlockItemIds.CONNECTOR.item()).useBlockDescriptionPrefix());
    public static final Item SHADOW_DRAWER = new DrawerItem(ModBlocks.SHADOW_DRAWER, new Item.Properties().setId(ModBlockItemIds.SHADOW_DRAWER.item()).useBlockDescriptionPrefix());
    public static final Item COMPACTING_DRAWER = new DrawerItem(ModBlocks.COMPACTING_DRAWER, new Item.Properties().setId(ModBlockItemIds.COMPACTING_DRAWER.item()).useBlockDescriptionPrefix());
    public static final Item ACCESS_POINT = new BlockItem(ModBlocks.ACCESS_POINT, new Item.Properties().setId(ModBlockItemIds.ACCESS_POINT.item()).useBlockDescriptionPrefix());
    
    public static final Item UPGRADE_FRAME = new Item(new Item.Properties().setId(ModItemIds.UPGRADE_FRAME));
    public static final UpgradeItem T1_UPGRADE = new UpgradeItem(new Item.Properties().setId(ModItemIds.T1_UPGRADE), id("item/t1_upgrade"), 1);
    public static final UpgradeItem T2_UPGRADE = new UpgradeItem(new Item.Properties().setId(ModItemIds.T2_UPGRADE), id("item/t2_upgrade"), 2);
    public static final UpgradeItem T3_UPGRADE = new UpgradeItem(new Item.Properties().setId(ModItemIds.T3_UPGRADE), id("item/t3_upgrade"), 3);
    public static final UpgradeItem T4_UPGRADE = new UpgradeItem(new Item.Properties().setId(ModItemIds.T4_UPGRADE), id("item/t4_upgrade"), 4);
    public static final UpgradeItem CREATIVE_UPGRADE = new UpgradeItem(new Item.Properties().setId(ModItemIds.CREATIVE_UPGRADE), id("item/creative_upgrade"), _ -> Long.MAX_VALUE);
    public static final LimiterItem LIMITER = new LimiterItem(new Item.Properties().setId(ModItemIds.LIMITER));
    public static final Item LOCK = new Item(new Item.Properties().setId(ModItemIds.LOCK));
    public static final Item DUPE_WAND = new Item(new Item.Properties().setId(ModItemIds.DUPE_WAND));

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, ModBlockItemIds.SINGLE_DRAWER.item(), SINGLE_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, ModBlockItemIds.DOUBLE_DRAWER.item(), DOUBLE_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, ModBlockItemIds.QUAD_DRAWER.item(), QUAD_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, ModBlockItemIds.CONNECTOR.item(), CONNECTOR);
        Registry.register(BuiltInRegistries.ITEM, ModBlockItemIds.SHADOW_DRAWER.item(), SHADOW_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, ModBlockItemIds.COMPACTING_DRAWER.item(), COMPACTING_DRAWER);
        Registry.register(BuiltInRegistries.ITEM, ModBlockItemIds.ACCESS_POINT.item(), ACCESS_POINT);
        
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.UPGRADE_FRAME, UPGRADE_FRAME);
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.T1_UPGRADE, T1_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.T2_UPGRADE, T2_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.T3_UPGRADE, T3_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.T4_UPGRADE, T4_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.CREATIVE_UPGRADE, CREATIVE_UPGRADE);
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.LIMITER, LIMITER);
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.LOCK, LOCK);
        Registry.register(BuiltInRegistries.ITEM, ModItemIds.DUPE_WAND, DUPE_WAND);
    }
}
