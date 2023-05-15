package io.github.mattidragon.extendeddrawers.compacting;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class CompressionOverrideLoader extends JsonDataLoader {
    private static final Gson GSON = new Gson();
    private final CompressionRecipeManager compressionRecipeManager;

    public CompressionOverrideLoader(CompressionRecipeManager compressionRecipeManager) {
        super(GSON, "extended_drawers/compression_overrides");
        this.compressionRecipeManager = compressionRecipeManager;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        var overrides = new ArrayList<CompressionLadder>();
        for (var overrideEntry : prepared.entrySet()) {
            var id = overrideEntry.getKey();
            var json = overrideEntry.getValue();

            try {
                var values = JsonHelper.asObject(json, "compression override");
                var steps = new LinkedHashMap<ItemVariant, Integer>();
                var currentCompression = 0;

                for (var stepEntry : values.entrySet()) {
                    var item = parseItem(stepEntry.getKey());
                    var compressionAmount = JsonHelper.asInt(stepEntry.getValue(), "compression amount for " + stepEntry.getKey());
                    if (compressionAmount == 0) throw new JsonParseException("Illegal compression amount for " + stepEntry.getKey() + ": 0");
                    if (compressionAmount <= currentCompression) throw new JsonParseException("Illegal compression amount for %s: %d, amounts should always rise".formatted(stepEntry.getKey(), compressionAmount));
                    currentCompression = compressionAmount;

                    steps.put(item, compressionAmount);
                }

                if (steps.isEmpty()) {
                    continue; // Allows disabling overrides using empty files instead of load conditions or pack filters
                }

                overrides.add(new CompressionLadder(steps.entrySet()
                        .stream()
                        .map(entry -> new CompressionLadder.Step(entry.getKey(), entry.getValue()))
                        .toList()));
            } catch (IllegalArgumentException | JsonParseException e) {
                ExtendedDrawers.LOGGER.error("Parsing error loading compression override {}", id, e);
            }
        }
        compressionRecipeManager.setOverrides(overrides);
    }

    private ItemVariant parseItem(String data) {
        try {
            var reader = new StringReader(data);
            var result = ItemStringReader.item(CommandRegistryWrapper.of(Registry.ITEM), reader);
            if (reader.getRemainingLength() != 0) {
                throw new JsonParseException("Failed to parse item, found trailing data: '%s'".formatted(reader.getRemaining()));
            }

            return ItemVariant.of(result.item().value(), result.nbt());
        } catch (CommandSyntaxException e) {
            throw new JsonParseException("Failed to parse item", e);
        }
    }
}
