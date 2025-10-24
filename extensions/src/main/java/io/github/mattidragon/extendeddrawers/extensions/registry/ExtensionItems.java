package io.github.mattidragon.extendeddrawers.extensions.registry;

import io.github.mattidragon.extendeddrawers.extensions.item.EnderConnectorLinkerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class ExtensionItems {
    public static final BlockItem DRAWER_BARREL = new BlockItem(ExtensionBlocks.DRAWER_BARREL, new Item.Settings().registryKey(key("drawer_barrel")).useBlockPrefixedTranslationKey());
    public static final BlockItem ENDER_CONNECTOR = new BlockItem(ExtensionBlocks.ENDER_CONNECTOR, new Item.Settings().registryKey(key("ender_connector")).useBlockPrefixedTranslationKey());

    public static final Item ENDER_CONNECTOR_LINKER = new EnderConnectorLinkerItem(new Item.Settings().registryKey(key("ender_connector_linker")));

    public static void register() {
        Registry.register(Registries.ITEM, id("drawer_barrel"), DRAWER_BARREL);
        Registry.register(Registries.ITEM, id("ender_connector"), ENDER_CONNECTOR);
        Registry.register(Registries.ITEM, id("ender_connector_linker"), ENDER_CONNECTOR_LINKER);
    }

    private static RegistryKey<Item> key(String path) {
        return RegistryKey.of(RegistryKeys.ITEM, id(path));
    }
}
