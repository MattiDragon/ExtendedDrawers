package io.github.mattidragon.extendeddrawers.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModTags {
    public static class ItemTags {
        public static final TagKey<Item> DRAWERS = TagKey.create(Registries.ITEM, id("drawers"));
        public static final TagKey<Item> UPGRADES = TagKey.create(Registries.ITEM, id("upgrade"));
        public static final TagKey<Item> TOGGLE_LOCK = TagKey.create(Registries.ITEM, id("toggle/lock"));
        public static final TagKey<Item> TOGGLE_VOIDING = TagKey.create(Registries.ITEM, id("toggle/voiding"));
        public static final TagKey<Item> TOGGLE_HIDDEN = TagKey.create(Registries.ITEM, id("toggle/hidden"));
        public static final TagKey<Item> TOGGLE_DUPING = TagKey.create(Registries.ITEM, id("toggle/duping"));
    }
    
    public static class BlockTags {
        public static final TagKey<Block> DRAWERS = TagKey.create(Registries.BLOCK, id("drawers"));
        public static final TagKey<Block> NETWORK_COMPONENTS = TagKey.create(Registries.BLOCK, id("network_components"));
    }
}
