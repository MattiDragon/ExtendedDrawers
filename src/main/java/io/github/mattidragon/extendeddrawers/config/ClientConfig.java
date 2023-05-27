package io.github.mattidragon.extendeddrawers.config;

import io.github.mattidragon.mconfig.config.*;

@NonReloadable
@Comment("This is the client config. It contains cosmetic and performance settings.\nThey shouldn't affect gameplay\n")
public record ClientConfig(
        @Comment("The render distance of the item icon on drawers")
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
        float textOffset
) {
    public static final Config<ClientConfig> HANDLE = ConfigManager.register(ConfigType.CLIENT, "extended_drawers", new ClientConfig(64, 16, 32, false, 0.4f, 1f, 0.5f, 1f, 0.2f));

    public float itemScale(boolean small) {
        return small ? smallItemScale : largeItemScale;
    }

    public float textScale(boolean small) {
        return small ? smallTextScale : largeTextScale;
    }
}
