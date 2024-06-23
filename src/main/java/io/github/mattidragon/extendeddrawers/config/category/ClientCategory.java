package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.configloader.api.AlwaysSerializedOptionalFieldCodec;
import io.github.mattidragon.configloader.api.GenerateMutable;
import net.minecraft.util.Identifier;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

@GenerateMutable
public record ClientCategory(int itemRenderDistance,
                             int iconRenderDistance,
                             int textRenderDistance,
                             boolean displayEmptyCount,
                             LayoutGroup layout,
                             IconGroup icons) implements MutableClientCategory.Source {
    public static final ClientCategory DEFAULT = new ClientCategory(64, 16, 32, false, LayoutGroup.DEFAULT, IconGroup.DEFAULT);

    public static final Codec<ClientCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AlwaysSerializedOptionalFieldCodec.create(Codec.INT, "itemRenderDistance", DEFAULT.itemRenderDistance).forGetter(ClientCategory::itemRenderDistance),
            AlwaysSerializedOptionalFieldCodec.create(Codec.INT, "iconRenderDistance", DEFAULT.iconRenderDistance).forGetter(ClientCategory::iconRenderDistance),
            AlwaysSerializedOptionalFieldCodec.create(Codec.INT, "textRenderDistance", DEFAULT.textRenderDistance).forGetter(ClientCategory::textRenderDistance),
            AlwaysSerializedOptionalFieldCodec.create(Codec.BOOL, "displayEmptyCount", DEFAULT.displayEmptyCount).forGetter(ClientCategory::displayEmptyCount),
            AlwaysSerializedOptionalFieldCodec.create(LayoutGroup.CODEC, "layout", DEFAULT.layout).forGetter(ClientCategory::layout),
            AlwaysSerializedOptionalFieldCodec.create(IconGroup.CODEC, "icons", DEFAULT.icons).forGetter(ClientCategory::icons)
    ).apply(instance, ClientCategory::new));

    @GenerateMutable
    public record IconGroup(Identifier lockedIcon,
                            Identifier voidingIcon,
                            Identifier hiddenIcon,
                            Identifier dupingIcon) implements MutableClientCategory.MutableIconGroup.Source {
        private static final IconGroup DEFAULT = new IconGroup(id("item/lock"), Identifier.ofVanilla("item/lava_bucket"), Identifier.ofVanilla("item/black_dye"), id("item/dupe_wand"));

        public static final Codec<IconGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AlwaysSerializedOptionalFieldCodec.create(Identifier.CODEC, "lockedIcon", DEFAULT.lockedIcon).forGetter(IconGroup::lockedIcon),
                AlwaysSerializedOptionalFieldCodec.create(Identifier.CODEC, "voidingIcon", DEFAULT.voidingIcon).forGetter(IconGroup::voidingIcon),
                AlwaysSerializedOptionalFieldCodec.create(Identifier.CODEC, "hiddenIcon", DEFAULT.hiddenIcon).forGetter(IconGroup::hiddenIcon),
                AlwaysSerializedOptionalFieldCodec.create(Identifier.CODEC, "dupingIcon", DEFAULT.dupingIcon).forGetter(IconGroup::dupingIcon)
        ).apply(instance, IconGroup::new));
    }

    @GenerateMutable
    public record LayoutGroup(float smallItemScale,
                              float largeItemScale,
                              float smallTextScale,
                              float largeTextScale,
                              float textOffset) implements MutableClientCategory.MutableLayoutGroup.Source {
        private static final LayoutGroup DEFAULT = new LayoutGroup(0.4f, 1f, 0.5f, 1f, 0.8f);

        public static final Codec<LayoutGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                AlwaysSerializedOptionalFieldCodec.create(Codec.FLOAT, "smallItemScale", DEFAULT.smallItemScale).forGetter(LayoutGroup::smallItemScale),
                AlwaysSerializedOptionalFieldCodec.create(Codec.FLOAT, "largeItemScale", DEFAULT.largeItemScale).forGetter(LayoutGroup::largeItemScale),
                AlwaysSerializedOptionalFieldCodec.create(Codec.FLOAT, "smallTextScale", DEFAULT.smallTextScale).forGetter(LayoutGroup::smallTextScale),
                AlwaysSerializedOptionalFieldCodec.create(Codec.FLOAT, "largeTextScale", DEFAULT.largeTextScale).forGetter(LayoutGroup::largeTextScale),
                AlwaysSerializedOptionalFieldCodec.create(Codec.FLOAT, "textOffset", DEFAULT.textOffset).forGetter(LayoutGroup::textOffset)
        ).apply(instance, LayoutGroup::new));

        public float itemScale(boolean small) {
            return small ? smallItemScale : largeItemScale;
        }

        public float textScale(boolean small) {
            return small ? smallTextScale : largeTextScale;
        }
    }
}
