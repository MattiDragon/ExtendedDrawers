package io.github.mattidragon.extendeddrawers.config;

import io.github.mattidragon.mconfig.config.Comment;
import io.github.mattidragon.mconfig.config.Config;
import io.github.mattidragon.mconfig.config.ConfigManager;
import io.github.mattidragon.mconfig.config.ConfigType;

public record CommonConfig(
        @Comment("The max distance that blocks will search for other blocks on a network")
        int networkSearchDistance,
        @Comment("The max time between the clicks of a double insert")
        int insertAllTime,
        @Comment("Whether to add a small cooldown to extractions to avoid double ones caused by a vanilla bug")
        boolean deduplicateExtraction
) {
    public static final Config<CommonConfig> HANDLE = ConfigManager.register(ConfigType.COMMON, "extended_drawers", new CommonConfig(64, 10, true));
}
