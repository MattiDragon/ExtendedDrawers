package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.item.LockItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ModItems {
    public static final Item SINGLE_DRAWER = new BlockItem(ModBlocks.SINGLE_DRAWER, new FabricItemSettings());
    public static final Item DOUBLE_DRAWER = new BlockItem(ModBlocks.DOUBLE_DRAWER, new FabricItemSettings());
    public static final Item QUAD_DRAWER = new BlockItem(ModBlocks.QUAD_DRAWER, new FabricItemSettings());
    public static final Item CONTROLLER = new BlockItem(ModBlocks.CONTROLLER, new FabricItemSettings());
    
    public static final Item LOCK = new LockItem(new FabricItemSettings());
    
    public static void register() {
        Registry.register(Registry.ITEM, id("single_drawer"), SINGLE_DRAWER);
        Registry.register(Registry.ITEM, id("double_drawer"), DOUBLE_DRAWER);
        Registry.register(Registry.ITEM, id("quad_drawer"), QUAD_DRAWER);
        Registry.register(Registry.ITEM, id("lock"), LOCK);
        Registry.register(Registry.ITEM, id("controller"), CONTROLLER);
    }
}
