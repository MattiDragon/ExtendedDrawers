package io.github.mattidragon.extendeddrawers.compacting;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompressionOverrideLoader extends SimpleReloadListener<Map<Identifier, CompressionLadder>> {
    public static final StateKey<List<CompressionLadder>> STATE_KEY = new StateKey<>();
    private static final FileToIdConverter FILE_TO_ID_CONVERTER = FileToIdConverter.json("extended_drawers/compression_overrides");

    private final DynamicOps<JsonElement> ops;

    public CompressionOverrideLoader(HolderLookup.Provider provider) {
        ops = provider.createSerializationContext(JsonOps.INSTANCE);
    }

    @Override
    protected Map<Identifier, CompressionLadder> prepare(SharedState state) {
        Map<Identifier, CompressionLadder> result = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(state.resourceManager(), FILE_TO_ID_CONVERTER, this.ops, CompressionLadder.CODEC, result);
        return result;
    }

    @Override
    protected void apply(Map<Identifier, CompressionLadder> prepared, SharedState state) {
        state.set(STATE_KEY, List.copyOf(prepared.values()));
    }
}
