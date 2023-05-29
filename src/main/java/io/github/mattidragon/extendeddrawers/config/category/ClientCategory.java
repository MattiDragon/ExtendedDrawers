package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import static io.github.mattidragon.extendeddrawers.config.ConfigData.defaultingFieldOf;

public record ClientCategory(int itemRenderDistance,
                             int iconRenderDistance,
                             int textRenderDistance,
                             boolean displayEmptyCount,
                             LayoutGroup layout) {
    public static final ClientCategory DEFAULT = new ClientCategory(64, 16, 32, false, new LayoutGroup(0.4f, 1f, 0.5f, 1f, 0.8f));

    public static final Codec<ClientCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            defaultingFieldOf(Codec.INT, "itemRenderDistance", DEFAULT.itemRenderDistance).forGetter(ClientCategory::itemRenderDistance),
            defaultingFieldOf(Codec.INT, "iconRenderDistance", DEFAULT.iconRenderDistance).forGetter(ClientCategory::iconRenderDistance),
            defaultingFieldOf(Codec.INT, "textRenderDistance", DEFAULT.textRenderDistance).forGetter(ClientCategory::textRenderDistance),
            defaultingFieldOf(Codec.BOOL, "displayEmptyCount", DEFAULT.displayEmptyCount).forGetter(ClientCategory::displayEmptyCount),
            defaultingFieldOf(LayoutGroup.CODEC, "layout", DEFAULT.layout).forGetter(ClientCategory::layout)
    ).apply(instance, ClientCategory::new));

    public Mutable toMutable() {
        return new Mutable(this);
    }

    public record LayoutGroup(float smallItemScale,
                              float largeItemScale,
                              float smallTextScale,
                              float largeTextScale,
                              float textOffset) {
        public static final Codec<LayoutGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                defaultingFieldOf(Codec.FLOAT, "smallItemScale", DEFAULT.layout.smallItemScale).forGetter(LayoutGroup::smallItemScale),
                defaultingFieldOf(Codec.FLOAT, "largeItemScale", DEFAULT.layout.largeItemScale).forGetter(LayoutGroup::largeItemScale),
                defaultingFieldOf(Codec.FLOAT, "smallTextScale", DEFAULT.layout.smallTextScale).forGetter(LayoutGroup::smallTextScale),
                defaultingFieldOf(Codec.FLOAT, "largeTextScale", DEFAULT.layout.largeTextScale).forGetter(LayoutGroup::largeTextScale),
                defaultingFieldOf(Codec.FLOAT, "textOffset", DEFAULT.layout.textOffset).forGetter(LayoutGroup::textOffset)
        ).apply(instance, LayoutGroup::new));

        public float itemScale(boolean small) {
            return small ? smallItemScale : largeItemScale;
        }

        public float textScale(boolean small) {
            return small ? smallTextScale : largeTextScale;
        }

        public Mutable toMutable() {
            return new Mutable(this);
        }

        public static final class Mutable {
            public float smallItemScale;
            public float largeItemScale;
            public float smallTextScale;
            public float largeTextScale;
            public float textOffset;

            public Mutable(LayoutGroup values) {
                this.smallItemScale = values.smallItemScale;
                this.largeItemScale = values.largeItemScale;
                this.smallTextScale = values.smallTextScale;
                this.largeTextScale = values.largeTextScale;
                this.textOffset = values.textOffset;
            }

            public LayoutGroup toImmutable() {
                return new LayoutGroup(smallItemScale, largeItemScale, smallTextScale, largeTextScale, textOffset);
            }
        }
    }

    public static final class Mutable {
        public int itemRenderDistance;
        public int iconRenderDistance;
        public int textRenderDistance;
        public boolean displayEmptyCount;
        public LayoutGroup.Mutable layout;

        private Mutable(ClientCategory values) {
            this.itemRenderDistance = values.itemRenderDistance;
            this.iconRenderDistance = values.iconRenderDistance;
            this.textRenderDistance = values.textRenderDistance;
            this.displayEmptyCount = values.displayEmptyCount;
            this.layout = values.layout.toMutable();
        }

        public ClientCategory toImmutable() {
            return new ClientCategory(itemRenderDistance, iconRenderDistance, textRenderDistance, displayEmptyCount, layout.toImmutable());
        }
    }
}
