package io.github.mattidragon.extendeddrawers.extensions.item;

import io.github.mattidragon.extendeddrawers.extensions.block.ExtensionBlockItemIds;
import io.github.mattidragon.extendeddrawers.extensions.block.ExtensionBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ExtensionItems {
    public static final BlockItem DRAWER_BARREL = new BlockItem(ExtensionBlocks.DRAWER_BARREL, new Item.Properties().setId(ExtensionBlockItemIds.DRAWER_BARREL.item()).useBlockDescriptionPrefix());
    public static final BlockItem ENDER_CONNECTOR = new BlockItem(ExtensionBlocks.ENDER_CONNECTOR, new Item.Properties().setId(ExtensionBlockItemIds.ENDER_CONNECTOR.item()).useBlockDescriptionPrefix());

    public static final Item ENDER_CONNECTOR_LINKER = new EnderConnectorLinkerItem(new Item.Properties().setId(ExtensionItemIds.ENDER_CONNECTOR_LINKER));

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, ExtensionBlockItemIds.DRAWER_BARREL.item(), DRAWER_BARREL);
        Registry.register(BuiltInRegistries.ITEM, ExtensionBlockItemIds.ENDER_CONNECTOR.item(), ENDER_CONNECTOR);
        Registry.register(BuiltInRegistries.ITEM, ExtensionItemIds.ENDER_CONNECTOR_LINKER, ENDER_CONNECTOR_LINKER);
    }
}
