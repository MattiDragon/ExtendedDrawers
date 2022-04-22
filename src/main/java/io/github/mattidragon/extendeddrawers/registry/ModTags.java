package io.github.mattidragon.extendeddrawers.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModTags {
    public static class ItemTags {
        public static final TagKey<Item> DRAWERS = TagKey.of(Registry.ITEM_KEY, id("drawers"));
        
    }
    
    public static class BlockTags {
        public static final TagKey<Block> DRAWERS = TagKey.of(Registry.BLOCK_KEY, id("drawers"));
        public static final TagKey<Block> NETWORK_COMPONENTS = TagKey.of(Registry.BLOCK_KEY, id("network_components"));
    
    }
}
