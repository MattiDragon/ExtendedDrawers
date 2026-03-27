package io.github.mattidragon.extendeddrawers.extensions.registry;

import io.github.mattidragon.extendeddrawers.extensions.item.EnderConnectorLinkerItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class ExtensionItems {
    public static final BlockItem DRAWER_BARREL = new BlockItem(ExtensionBlocks.DRAWER_BARREL, new Item.Properties().setId(key("drawer_barrel")).useBlockDescriptionPrefix());
    public static final BlockItem ENDER_CONNECTOR = new BlockItem(ExtensionBlocks.ENDER_CONNECTOR, new Item.Properties().setId(key("ender_connector")).useBlockDescriptionPrefix());

    public static final Item ENDER_CONNECTOR_LINKER = new EnderConnectorLinkerItem(new Item.Properties().setId(key("ender_connector_linker")));

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, id("drawer_barrel"), DRAWER_BARREL);
        Registry.register(BuiltInRegistries.ITEM, id("ender_connector"), ENDER_CONNECTOR);
        Registry.register(BuiltInRegistries.ITEM, id("ender_connector_linker"), ENDER_CONNECTOR_LINKER);
    }

    private static ResourceKey<Item> key(String path) {
        return ResourceKey.create(Registries.ITEM, id(path));
    }
}
