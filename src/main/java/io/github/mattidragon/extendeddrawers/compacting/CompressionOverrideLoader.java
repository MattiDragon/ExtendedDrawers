package io.github.mattidragon.extendeddrawers.compacting;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;

public class CompressionOverrideLoader extends SimpleJsonResourceReloadListener<CompressionLadder> {
    private final ServerCompressionRecipeManager compressionRecipeManager;

    public CompressionOverrideLoader(ServerCompressionRecipeManager compressionRecipeManager) {
        super(CompressionLadder.CODEC, FileToIdConverter.json("extended_drawers/compression_overrides"));
        this.compressionRecipeManager = compressionRecipeManager;
    }

    @Override
    protected void apply(Map<Identifier, CompressionLadder> prepared, ResourceManager manager, ProfilerFiller profiler) {
        compressionRecipeManager.setOverrides(List.copyOf(prepared.values()));
    }
}
