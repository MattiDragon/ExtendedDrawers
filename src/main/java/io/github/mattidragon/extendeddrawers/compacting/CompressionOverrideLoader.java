package io.github.mattidragon.extendeddrawers.compacting;

import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;

public class CompressionOverrideLoader extends JsonDataLoader<CompressionLadder> {
    private final ServerCompressionRecipeManager compressionRecipeManager;

    public CompressionOverrideLoader(ServerCompressionRecipeManager compressionRecipeManager) {
        super(CompressionLadder.CODEC, ResourceFinder.json("extended_drawers/compression_overrides"));
        this.compressionRecipeManager = compressionRecipeManager;
    }

    @Override
    protected void apply(Map<Identifier, CompressionLadder> prepared, ResourceManager manager, Profiler profiler) {
        compressionRecipeManager.setOverrides(List.copyOf(prepared.values()));
    }
}
