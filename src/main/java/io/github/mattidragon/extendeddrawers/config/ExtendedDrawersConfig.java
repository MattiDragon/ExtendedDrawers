package io.github.mattidragon.extendeddrawers.config;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ExtendedDrawersConfig {
    /**
     * Called whenever the config is changes. Used for resetting skins on player heads
     */
    public static final Event<OnChange> ON_CHANGE = EventFactory.createArrayBacked(OnChange.class,
            listeners -> config -> {
                for (var listener : listeners) {
                    listener.onChange(config);
                }
            });
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("extended_drawers.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private static boolean prepared = false;
    private static ConfigData instance = ConfigData.DEFAULT;

    private ExtendedDrawersConfig() {}

    public static ConfigData get() {
        // Use default config in datagen to avoid it getting out of date.
        if (System.getProperty("fabric-api.datagen") != null) {
            return ConfigData.DEFAULT;
        }

        prepare();
        return instance;
    }

    public static void set(ConfigData config) {
        instance = config;
        ON_CHANGE.invoker().onChange(config);
        save();
    }

    private static void prepare() {
        if (prepared) return;
        prepared = true;

        if (Files.exists(PATH)) {
            load();
        } else {
            save();
        }
        registerCommand();
    }

    private static void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var root = CommandManager.literal("extended_drawers")
                    .requires(source -> source.hasPermissionLevel(2));

            root.then(CommandManager.literal("reload")
                    .executes(context -> {
                        try {
                            load();
                        } catch (RuntimeException e) {
                            var error = Text.translatable("command.extended_drawers.reload.fail")
                                    .fillStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(e.toString()))));
                            context.getSource().sendError(error);
                            ExtendedDrawers.LOGGER.error("Failed to reload config", e);
                            return 0;
                        }
                        context.getSource().sendFeedback(() -> Text.translatable("command.extended_drawers.reload.success"), true);
                        return 1;
                    }));

            dispatcher.register(root);
        });
    }

    private static void save() {
        ConfigData.CODEC.encodeStart(JsonOps.INSTANCE, instance)
                .resultOrPartial(ExtendedDrawers.LOGGER::error)
                .ifPresent(ExtendedDrawersConfig::write);
    }

    private static void write(JsonElement data) {
        try (var out = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(data, out);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save extended drawers config", e);
        }
    }

    private static void load() {
        try (var in = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
            var json = GSON.fromJson(in, JsonObject.class);
            var result = ConfigData.CODEC.parse(JsonOps.INSTANCE, json);

            instance = result.mapError("Extended drawers config failed to load: %s. Delete the file or invalid values to regenerate defaults."::formatted)
                    .getOrThrow(false, ExtendedDrawers.LOGGER::error);

            ON_CHANGE.invoker().onChange(instance);

            ConfigData.CODEC.encodeStart(JsonOps.INSTANCE, instance)
                    .resultOrPartial(ExtendedDrawers.LOGGER::error)
                    .filter(o -> !json.equals(o)) // Don't save if no changes in json
                    .ifPresent(ExtendedDrawersConfig::write);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load extended drawers config due to io error", e);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Extended drawers config has a syntax errors", e);
        }
    }

    @FunctionalInterface
    public interface OnChange {
        void onChange(ConfigData config);
    }
}
