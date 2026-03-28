package io.github.mattidragon.extendeddrawers.compacting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

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
                            Item.CODEC.flatComapMap(
                                    entry -> ItemVariant.of(entry.value()),
                                    variant -> variant.hasComponents() ? DataResult.error(() -> "Cannot serialize components") : DataResult.success(variant.typeHolder()))
                    ),
                    Codec.intRange(1, Integer.MAX_VALUE))
            .xmap(map -> new CompressionLadder(map.entrySet().stream().map(entry -> new Step(entry.getKey(), entry.getValue())).toList()),
                    ladder -> ladder.steps.stream().collect(Collectors.toMap(Step::item, Step::size, (a, _) -> a, Object2ObjectArrayMap::new)))
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
    public static final StreamCodec<RegistryFriendlyByteBuf, CompressionLadder> PACKET_CODEC = StreamCodec.composite(
            Step.PACKET_CODEC.apply(ByteBufCodecs.list()), CompressionLadder::steps,
            CompressionLadder::new);

    public CompressionLadder(List<Step> steps) {
        this.steps = List.copyOf(steps);
    }

    /**
     * @param item The item at this step.
     * @param size The amount of the first step required to craft this one.
     */
    public record Step(ItemVariant item, int size) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Step> PACKET_CODEC = StreamCodec.composite(
                ItemVariant.PACKET_CODEC, Step::item,
                ByteBufCodecs.VAR_INT, Step::size,
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
