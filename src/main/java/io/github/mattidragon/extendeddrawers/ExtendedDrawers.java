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
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedDrawers implements ModInitializer {
    public static final String MOD_ID = "extended_drawers";
    public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ConfigManager<ConfigData> CONFIG = ConfigManager.create(ConfigData.CODEC, ConfigData.DEFAULT, MOD_ID);
    public static ShiftAccess SHIFT_ACCESS = () -> false;

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
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
        ResourceManagerHelper.registerBuiltinResourcePack(id("alt"), MOD_CONTAINER, Text.translatable("resourcepack.extended_drawers.alt"), ResourcePackActivationType.NORMAL);
        ResourceManagerHelper.registerBuiltinResourcePack(id("dev"), MOD_CONTAINER, Text.translatable("resourcepack.extended_drawers.programmer_art"), ResourcePackActivationType.NORMAL);
    }

    private static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var root = CommandManager.literal("extended_drawers")
                    .requires(source -> source.hasPermissionLevel(2));

            root.then(CommandManager.literal("reload")
                    .executes(context -> {
                        var error = CONFIG.reload();
                        if (error.isEmpty()) {
                            context.getSource().sendFeedback(() -> Text.translatable("command.extended_drawers.reload.success"), true);
                            return 1;
                        }
                        var message = Text.translatable("command.extended_drawers.reload.fail")
                                .fillStyle(Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(Text.literal(error.get().toString()))));
                        context.getSource().sendError(message);
                        LOGGER.error("Failed to reload config", error.get());
                        return 0;
                    }));

            dispatcher.register(root);
        });
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
                    entries.add(ModItems.CREATIVE_UPGRADE);
                    entries.add(ModItems.UPGRADE_FRAME);
                    entries.add(ModItems.LOCK);
                    entries.add(ModItems.LIMITER);
                    entries.add(ModItems.DUPE_WAND);
                })
                .build());
    }
}
