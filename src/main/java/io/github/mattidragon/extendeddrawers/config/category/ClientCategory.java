package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.mconfig.config.Comment;

import static io.github.mattidragon.extendeddrawers.config.ConfigData.defaultingFieldOf;

public record ClientCategory(@Comment("The render distance of the item icon on drawers")
                             int itemRenderDistance,
                             @Comment("The render distance of the lock and upgrade icons on drawers")
                             int iconRenderDistance,
                             @Comment("The render distance of the number of items on the drawers")
                             int textRenderDistance,
                             @Comment("Whether to display the amount of items on empty drawers")
                             boolean displayEmptyCount,
                             @Comment("The scale at which to render the items for small slots")
                             float smallItemScale,
                             @Comment("The scale at which to render the items for large slots")
                             float largeItemScale,
                             @Comment("The scale at which to render the text for small slots")
                             float smallTextScale,
                             @Comment("The scale at which to render the text for large slots")
                             float largeTextScale,
                             @Comment("Offset from the bottom of the slot to move the text by")
                             float textOffset) {
    public static final ClientCategory DEFAULT = new ClientCategory(64, 16, 32, false, 0.4f, 1f, 0.5f, 1f, 0.2f);

    public static final Codec<ClientCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            defaultingFieldOf(Codec.INT, "itemRenderDistance", DEFAULT.itemRenderDistance).forGetter(ClientCategory::itemRenderDistance),
            defaultingFieldOf(Codec.INT, "iconRenderDistance", DEFAULT.iconRenderDistance).forGetter(ClientCategory::iconRenderDistance),
            defaultingFieldOf(Codec.INT, "textRenderDistance", DEFAULT.textRenderDistance).forGetter(ClientCategory::textRenderDistance),
            defaultingFieldOf(Codec.BOOL, "displayEmptyCount", DEFAULT.displayEmptyCount).forGetter(ClientCategory::displayEmptyCount),
            defaultingFieldOf(Codec.FLOAT, "smallItemScale", DEFAULT.smallItemScale).forGetter(ClientCategory::smallItemScale),
            defaultingFieldOf(Codec.FLOAT, "largeItemScale", DEFAULT.largeItemScale).forGetter(ClientCategory::largeItemScale),
            defaultingFieldOf(Codec.FLOAT, "smallTextScale", DEFAULT.smallTextScale).forGetter(ClientCategory::smallTextScale),
            defaultingFieldOf(Codec.FLOAT, "largeTextScale", DEFAULT.largeTextScale).forGetter(ClientCategory::largeTextScale),
            defaultingFieldOf(Codec.FLOAT, "textOffset", DEFAULT.textOffset).forGetter(ClientCategory::textOffset)
    ).apply(instance, ClientCategory::new));

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
        public int itemRenderDistance;
        public int iconRenderDistance;
        public int textRenderDistance;
        public boolean displayEmptyCount;
        public float smallItemScale;
        public float largeItemScale;
        public float smallTextScale;
        public float largeTextScale;
        public float textOffset;

        private Mutable(ClientCategory values) {
            this.itemRenderDistance = values.itemRenderDistance;
            this.iconRenderDistance = values.iconRenderDistance;
            this.textRenderDistance = values.textRenderDistance;
            this.displayEmptyCount = values.displayEmptyCount;
            this.smallItemScale = values.smallItemScale;
            this.largeItemScale = values.largeItemScale;
            this.smallTextScale = values.smallTextScale;
            this.largeTextScale = values.largeTextScale;
            this.textOffset = values.textOffset;
        }

        public ClientCategory toImmutable() {
            return new ClientCategory(itemRenderDistance, iconRenderDistance, textRenderDistance, displayEmptyCount, smallItemScale, largeItemScale, smallTextScale, largeTextScale, textOffset);
        }
    }
}
