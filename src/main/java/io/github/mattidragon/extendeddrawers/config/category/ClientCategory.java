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
                             @Comment("The scale at which to render the items")
                             float itemScale) {
    public static final ClientCategory DEFAULT = new ClientCategory(64, 16, 32, false, 1.0f);

    public static final Codec<ClientCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            defaultingFieldOf(Codec.INT, "itemRenderDistance", DEFAULT.itemRenderDistance).forGetter(ClientCategory::itemRenderDistance),
            defaultingFieldOf(Codec.INT, "iconRenderDistance", DEFAULT.iconRenderDistance).forGetter(ClientCategory::iconRenderDistance),
            defaultingFieldOf(Codec.INT, "textRenderDistance", DEFAULT.textRenderDistance).forGetter(ClientCategory::textRenderDistance),
            defaultingFieldOf(Codec.BOOL, "displayEmptyCount", DEFAULT.displayEmptyCount).forGetter(ClientCategory::displayEmptyCount),
            defaultingFieldOf(Codec.FLOAT, "itemScale", DEFAULT.itemScale).forGetter(ClientCategory::itemScale)
    ).apply(instance, ClientCategory::new));

    public Mutable toMutable() {
        return new Mutable(this);
    }

    public static final class Mutable {
        public int itemRenderDistance;
        public int iconRenderDistance;
        public int textRenderDistance;
        public boolean displayEmptyCount;
        public float itemScale;

        private Mutable(ClientCategory values) {
            this.itemRenderDistance = values.itemRenderDistance;
            this.iconRenderDistance = values.iconRenderDistance;
            this.textRenderDistance = values.textRenderDistance;
            this.displayEmptyCount = values.displayEmptyCount;
            this.itemScale = values.itemScale;
        }

        public ClientCategory toImmutable() {
            return new ClientCategory(itemRenderDistance, iconRenderDistance, textRenderDistance, displayEmptyCount, itemScale);
        }
    }
}
