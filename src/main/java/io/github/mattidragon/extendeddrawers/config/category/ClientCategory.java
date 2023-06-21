package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.configloader.api.DefaultedFieldCodec;
import io.github.mattidragon.configloader.api.GenerateMutable;

@GenerateMutable
public record ClientCategory(int itemRenderDistance,
                             int iconRenderDistance,
                             int textRenderDistance,
                             boolean displayEmptyCount,
                             LayoutGroup layout) implements MutableClientCategory.Source {
    public static final ClientCategory DEFAULT = new ClientCategory(64, 16, 32, false, LayoutGroup.DEFAULT);

    public static final Codec<ClientCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DefaultedFieldCodec.of(Codec.INT, "itemRenderDistance", DEFAULT.itemRenderDistance).forGetter(ClientCategory::itemRenderDistance),
            DefaultedFieldCodec.of(Codec.INT, "iconRenderDistance", DEFAULT.iconRenderDistance).forGetter(ClientCategory::iconRenderDistance),
            DefaultedFieldCodec.of(Codec.INT, "textRenderDistance", DEFAULT.textRenderDistance).forGetter(ClientCategory::textRenderDistance),
            DefaultedFieldCodec.of(Codec.BOOL, "displayEmptyCount", DEFAULT.displayEmptyCount).forGetter(ClientCategory::displayEmptyCount),
            DefaultedFieldCodec.of(LayoutGroup.CODEC, "layout", DEFAULT.layout).forGetter(ClientCategory::layout)
    ).apply(instance, ClientCategory::new));

    @GenerateMutable
    public record LayoutGroup(float smallItemScale,
                              float largeItemScale,
                              float smallTextScale,
                              float largeTextScale,
                              float textOffset) implements MutableClientCategory.MutableLayoutGroup.Source {
        private static final LayoutGroup DEFAULT = new LayoutGroup(0.4f, 1f, 0.5f, 1f, 0.8f);

        public static final Codec<LayoutGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DefaultedFieldCodec.of(Codec.FLOAT, "smallItemScale", DEFAULT.smallItemScale).forGetter(LayoutGroup::smallItemScale),
                DefaultedFieldCodec.of(Codec.FLOAT, "largeItemScale", DEFAULT.largeItemScale).forGetter(LayoutGroup::largeItemScale),
                DefaultedFieldCodec.of(Codec.FLOAT, "smallTextScale", DEFAULT.smallTextScale).forGetter(LayoutGroup::smallTextScale),
                DefaultedFieldCodec.of(Codec.FLOAT, "largeTextScale", DEFAULT.largeTextScale).forGetter(LayoutGroup::largeTextScale),
                DefaultedFieldCodec.of(Codec.FLOAT, "textOffset", DEFAULT.textOffset).forGetter(LayoutGroup::textOffset)
        ).apply(instance, LayoutGroup::new));

        public float itemScale(boolean small) {
            return small ? smallItemScale : largeItemScale;
        }

        public float textScale(boolean small) {
            return small ? smallTextScale : largeTextScale;
        }
    }
}
