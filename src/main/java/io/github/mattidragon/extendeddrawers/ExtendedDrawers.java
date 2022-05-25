package io.github.mattidragon.extendeddrawers;

import io.github.mattidragon.extendeddrawers.config.ClientConfig;
import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.util.DrawerContentsLootFunction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

public class ExtendedDrawers implements ModInitializer {
    public static final String MOD_ID = "extended_drawers";
    @SuppressWarnings("Convert2MethodRef") // We can't load ModItems before this is done
    public static final ItemGroup MOD_GROUP = FabricItemGroupBuilder.create(id("main")).icon(() -> ModItems.SHADOW_DRAWER.getDefaultStack()).build();
    
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
    
    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        DrawerContentsLootFunction.register();
        ClientConfig.HANDLE.load();
        CommonConfig.HANDLE.load();
    }
}
