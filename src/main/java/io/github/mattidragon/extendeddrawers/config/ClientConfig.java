package io.github.mattidragon.extendeddrawers.config;

import io.github.mattidragon.mconfig.config.Comment;
import io.github.mattidragon.mconfig.config.Config;
import io.github.mattidragon.mconfig.config.ConfigManager;
import io.github.mattidragon.mconfig.config.ConfigType;

public record ClientConfig(
        @Comment("The render distance of the item icon on drawers")
        int itemRenderDistance,
        @Comment("The render distance of the lock and upgrade icons on drawers")
        int iconRenderDistance,
        @Comment("The render distance of the number of items on the drawers")
        int textRenderDistance
) {
    public static final Config<ClientConfig> HANDLE = ConfigManager.register(ConfigType.CLIENT, "extended_drawers", new ClientConfig(64, 16, 32));
}
