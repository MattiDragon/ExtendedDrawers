package io.github.mattidragon.extendeddrawers.compacting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a bidirectional ladder of compression recipes.
 * First step has the base item (e.g. nuggets) and a size of 1.
 * Each step after that has a new item (ingots, blocks) and a size that counts how many of the first tier is necessary (9, 81).
 */
public record CompressionLadder(List<Step> steps) {
    public static final Codec<CompressionLadder> CODEC = Codec.unboundedMap(
                    Codec.withAlternative(
                            ItemVariant.CODEC.validate(variant -> variant.isBlank() ? DataResult.error(() -> "Cannot use air") : DataResult.success(variant)),
                            Item.ENTRY_CODEC.flatComapMap(
                                    entry -> ItemVariant.of(entry.value()),
                                    variant -> variant.hasComponents() ? DataResult.error(() -> "Cannot serialize components") : DataResult.success(variant.getRegistryEntry()))
                    ),
                    Codec.intRange(1, Integer.MAX_VALUE))
            .xmap(map -> new CompressionLadder(map.entrySet().stream().map(entry -> new Step(entry.getKey(), entry.getValue())).toList()),
                    ladder -> ladder.steps.stream().collect(Collectors.toMap(Step::item, Step::size, (a, b) -> a, Object2ObjectArrayMap::new)))
            .validate(ladder -> {
                var prevSize = 1;
                for (var step : ladder.steps) {
                    if (step.size < prevSize) {
                        var finalPrevSize = prevSize;
                        return DataResult.error(() -> "Compression ladder entries must have increasing size order, but %s < %s".formatted(step.size, finalPrevSize));
                    }
                    prevSize = step.size;
                }
                return DataResult.success(ladder);
            });
    public static final PacketCodec<RegistryByteBuf, CompressionLadder> PACKET_CODEC = PacketCodec.tuple(
            Step.PACKET_CODEC.collect(PacketCodecs.toList()), CompressionLadder::steps,
            CompressionLadder::new);

    public CompressionLadder(List<Step> steps) {
        this.steps = List.copyOf(steps);
    }

    /**
     * @param item The item at this step.
     * @param size The amount of the first step required to craft this one.
     */
    public record Step(ItemVariant item, int size) {
        public static final PacketCodec<RegistryByteBuf, Step> PACKET_CODEC = PacketCodec.tuple(
                ItemVariant.PACKET_CODEC, Step::item,
                PacketCodecs.VAR_INT, Step::size,
                Step::new
        );
    }

    public int getPosition(ItemVariant item) {
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).item.equals(item))
                return i;
        }
        return -1;
    }
}
