package io.github.mattidragon.extendeddrawers;

import io.github.mattidragon.extendeddrawers.config.old.ClientConfig;
import io.github.mattidragon.extendeddrawers.config.old.CommonConfig;
import io.github.mattidragon.extendeddrawers.misc.DrawerContentsLootFunction;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.networking.CompressionOverrideSyncPacket;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedDrawers implements ModInitializer {
    public static final String MOD_ID = "extended_drawers";
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
    
    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModItems.register();
        DrawerContentsLootFunction.register();
        registerItemGroup();
        NetworkRegistry.register();
        CompressionOverrideSyncPacket.register();
        ClientConfig.HANDLE.load();
        CommonConfig.HANDLE.load();
        ResourceManagerHelper.registerBuiltinResourcePack(id("alt"), MOD_CONTAINER, Text.translatable("resourcepack.extended_drawers.alt"), ResourcePackActivationType.NORMAL);
        ResourceManagerHelper.registerBuiltinResourcePack(id("dev"), MOD_CONTAINER, Text.translatable("resourcepack.extended_drawers.programmer_art"), ResourcePackActivationType.NORMAL);
    }

    private void registerItemGroup() {
        Registry.register(Registries.ITEM_GROUP, id("main"), FabricItemGroup.builder()
                .icon(ModItems.SHADOW_DRAWER::getDefaultStack)
                .displayName(Text.translatable("itemGroup.extended_drawers.main"))
                .entries((context, entries) -> {
                    entries.add(ModBlocks.SINGLE_DRAWER);
                    entries.add(ModBlocks.DOUBLE_DRAWER);
                    entries.add(ModBlocks.QUAD_DRAWER);
                    entries.add(ModBlocks.CONNECTOR);
                    entries.add(ModBlocks.SHADOW_DRAWER);
                    entries.add(ModBlocks.COMPACTING_DRAWER);
                    entries.add(ModBlocks.ACCESS_POINT);

                    entries.add(ModItems.T1_UPGRADE);
                    entries.add(ModItems.T2_UPGRADE);
                    entries.add(ModItems.T3_UPGRADE);
                    entries.add(ModItems.T4_UPGRADE);
                    entries.add(ModItems.DOWNGRADE);
                    entries.add(ModItems.CREATIVE_UPGRADE);
                    entries.add(ModItems.UPGRADE_FRAME);
                    entries.add(ModItems.LOCK);
                })
                .build());
    }
}
