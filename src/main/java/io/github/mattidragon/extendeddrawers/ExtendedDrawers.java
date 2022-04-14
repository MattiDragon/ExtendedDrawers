package io.github.mattidragon.extendeddrawers;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class ExtendedDrawers implements ModInitializer {
    private static final String MOD_ID = "extended_drawers";
    
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
    
    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
    }
}
