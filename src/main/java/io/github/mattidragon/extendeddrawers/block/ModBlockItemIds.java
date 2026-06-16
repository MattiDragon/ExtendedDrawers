package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.minecraft.references.BlockItemId;

public class ModBlockItemIds {
    public static final BlockItemId SINGLE_DRAWER = id("single_drawer");
    public static final BlockItemId DOUBLE_DRAWER = id("double_drawer");
    public static final BlockItemId QUAD_DRAWER = id("quad_drawer");
    public static final BlockItemId CONNECTOR = id("connector");
    public static final BlockItemId ACCESS_POINT = id("access_point");
    public static final BlockItemId SHADOW_DRAWER = id("shadow_drawer");
    public static final BlockItemId COMPACTING_DRAWER = id("compacting_drawer");

    private static BlockItemId id(String path) {
        var id = ExtendedDrawers.id(path);
        return BlockItemId.create(id, id);
    }
}
