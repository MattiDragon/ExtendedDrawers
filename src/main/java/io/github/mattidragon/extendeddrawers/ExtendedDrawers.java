package io.github.mattidragon.extendeddrawers;

import io.github.mattidragon.configloader.api.ConfigManager;
import io.github.mattidragon.extendeddrawers.config.ConfigData;
import io.github.mattidragon.extendeddrawers.misc.DrawerCacheCommand;
import io.github.mattidragon.extendeddrawers.misc.ShiftAccess;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.networking.CompressionRecipeSyncPayload;
import io.github.mattidragon.extendeddrawers.networking.SetLimiterLimitPayload;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedDrawers implements ModInitializer {
    public static final String MOD_ID = "extended_drawers";
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConfigManager<ConfigData> CONFIG = ConfigManager.create(ConfigData.CODEC, ConfigData.DEFAULT, MOD_ID);
    public static ShiftAccess SHIFT_ACCESS = () -> false;

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        // Ensure that file is created
        CONFIG.get();

        ModBlocks.register();
        ModDataComponents.register();
        ModItems.register();
        ModRecipes.register();
        registerItemGroup();
        registerCommand();
        NetworkRegistry.register();
        CompressionRecipeSyncPayload.register();
        SetLimiterLimitPayload.register();
        DrawerCacheCommand.register();
        ResourceLoader.registerBuiltinPack(id("alt"), MOD_CONTAINER, Component.translatable("resourcepack.extended_drawers.alt"), PackActivationType.NORMAL);
        ResourceLoader.registerBuiltinPack(id("dev"), MOD_CONTAINER, Component.translatable("resourcepack.extended_drawers.programmer_art"), PackActivationType.NORMAL);
    }

    private static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            var root = Commands.literal("extended_drawers")
                    .requires(Commands.hasPermission(Commands.LEVEL_ADMINS));

            root.then(Commands.literal("reload")
                    .executes(context -> {
                        var error = CONFIG.reload();
                        if (error.isEmpty()) {
                            context.getSource().sendSuccess(() -> Component.translatable("command.extended_drawers.reload.success"), true);
                            return 1;
                        }
                        var message = Component.translatable("command.extended_drawers.reload.fail")
                                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Component.literal(error.get().toString()))));
                        context.getSource().sendFailure(message);
                        LOGGER.error("Failed to reload config", error.get());
                        return 0;
                    }));

            dispatcher.register(root);
        });
    }

    private void registerItemGroup() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id("main"), FabricCreativeModeTab.builder()
                .icon(ModItems.SHADOW_DRAWER::getDefaultInstance)
                .title(Component.translatable("itemGroup.extended_drawers.main"))
                .displayItems((_, entries) -> {
                    entries.accept(ModBlocks.SINGLE_DRAWER);
                    entries.accept(ModBlocks.DOUBLE_DRAWER);
                    entries.accept(ModBlocks.QUAD_DRAWER);
                    entries.accept(ModBlocks.CONNECTOR);
                    entries.accept(ModBlocks.SHADOW_DRAWER);
                    entries.accept(ModBlocks.COMPACTING_DRAWER);
                    entries.accept(ModBlocks.ACCESS_POINT);

                    entries.accept(ModItems.T1_UPGRADE);
                    entries.accept(ModItems.T2_UPGRADE);
                    entries.accept(ModItems.T3_UPGRADE);
                    entries.accept(ModItems.T4_UPGRADE);
                    entries.accept(ModItems.CREATIVE_UPGRADE);
                    entries.accept(ModItems.UPGRADE_FRAME);
                    entries.accept(ModItems.LOCK);
                    entries.accept(ModItems.LIMITER);
                    entries.accept(ModItems.DUPE_WAND);
                })
                .build());
    }
}
