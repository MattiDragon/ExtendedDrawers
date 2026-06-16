package io.github.mattidragon.extendeddrawers.extensions.block;

import io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions;
import net.minecraft.references.BlockItemId;

public class ExtensionBlockItemIds {
    public static final BlockItemId DRAWER_BARREL = id("drawer_barrel");
    public static final BlockItemId ENDER_CONNECTOR = id("ender_connector");

    private static BlockItemId id(String path) {
        var id = ExtendedDrawersExtensions.id(path);
        return BlockItemId.create(id, id);
    }
}
