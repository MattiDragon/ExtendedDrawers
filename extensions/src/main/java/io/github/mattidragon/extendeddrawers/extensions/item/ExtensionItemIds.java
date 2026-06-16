package io.github.mattidragon.extendeddrawers.extensions.item;

import io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ExtensionItemIds {
    public static final ResourceKey<Item> ENDER_CONNECTOR_LINKER = key("ender_connector_linker");

    private static ResourceKey<Item> key(String path) {
        return ResourceKey.create(Registries.ITEM, ExtendedDrawersExtensions.id(path));
    }
}
