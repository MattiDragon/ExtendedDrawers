package io.github.mattidragon.extendeddrawers.item;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {
    public static final ResourceKey<Item> UPGRADE_FRAME = key("upgrade_frame");
    public static final ResourceKey<Item> T1_UPGRADE = key("t1_upgrade");
    public static final ResourceKey<Item> T2_UPGRADE = key("t2_upgrade");
    public static final ResourceKey<Item> T3_UPGRADE = key("t3_upgrade");
    public static final ResourceKey<Item> T4_UPGRADE = key("t4_upgrade");
    public static final ResourceKey<Item> CREATIVE_UPGRADE = key("creative_upgrade");
    public static final ResourceKey<Item> LIMITER = key("limiter");
    public static final ResourceKey<Item> LOCK = key("lock");
    public static final ResourceKey<Item> DUPE_WAND = key("dupe_wand");

    private static ResourceKey<Item> key(String path) {
        return ResourceKey.create(Registries.ITEM, ExtendedDrawers.id(path));
    }
}
