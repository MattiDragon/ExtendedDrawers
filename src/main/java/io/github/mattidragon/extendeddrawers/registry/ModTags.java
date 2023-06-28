package io.github.mattidragon.extendeddrawers.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModTags {
    public static class ItemTags {
        public static final TagKey<Item> DRAWERS = TagKey.of(RegistryKeys.ITEM, id("drawers"));
        public static final TagKey<Item> UPGRADES = TagKey.of(RegistryKeys.ITEM, id("upgrade"));
        public static final TagKey<Item> TOGGLE_LOCK = TagKey.of(RegistryKeys.ITEM, id("toggle/lock"));
        public static final TagKey<Item> TOGGLE_VOIDING = TagKey.of(RegistryKeys.ITEM, id("toggle/voiding"));
        public static final TagKey<Item> TOGGLE_HIDDEN = TagKey.of(RegistryKeys.ITEM, id("toggle/hidden"));
        public static final TagKey<Item> TOGGLE_DUPING = TagKey.of(RegistryKeys.ITEM, id("toggle/duping"));
    }
    
    public static class BlockTags {
        public static final TagKey<Block> DRAWERS = TagKey.of(RegistryKeys.BLOCK, id("drawers"));
        public static final TagKey<Block> NETWORK_COMPONENTS = TagKey.of(RegistryKeys.BLOCK, id("network_components"));
    }
}
