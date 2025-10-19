package io.github.mattidragon.extendeddrawers.extensions.registry;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class ExtensionItems {
    public static final BlockItem DRAWER_BARREL = new BlockItem(ExtensionBlocks.DRAWER_BARREL, new Item.Settings().registryKey(key("drawer_barrel")).useBlockPrefixedTranslationKey());

    public static void register() {
        Registry.register(Registries.ITEM, id("drawer_barrel"), DRAWER_BARREL);
    }

    private static RegistryKey<Item> key(String path) {
        return RegistryKey.of(RegistryKeys.ITEM, id(path));
    }
}
