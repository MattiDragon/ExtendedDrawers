package io.github.mattidragon.extendeddrawers.config;

import io.github.mattidragon.mconfig.config.Comment;
import io.github.mattidragon.mconfig.config.Config;
import io.github.mattidragon.mconfig.config.ConfigManager;
import io.github.mattidragon.mconfig.config.ConfigType;

@Comment("This is the common config. It contains gameplay settings.\nIt isn't automatically synced, but it is recommended that the client has the same settings as the server\n")
public record CommonConfig(
        @Comment("The max distance that blocks will search for other blocks on a network")
        int networkSearchDistance,
        @Comment("The max time between the clicks of a double insert")
        int insertAllTime,
        @Comment("Whether to add a small cooldown to extractions to avoid double ones caused by a vanilla bug")
        boolean deduplicateExtraction,
        @Comment("How many items drawers are able to hold.")
        int defaultCapacity,
        @Comment("Wherther the stack size of the item should affect capacity")
        boolean stackSizeAffectsCapacity,
        @Comment("Wherther the amount of slots on a drawers should affect capacity")
        boolean slotCountAffectsCapacity
) {
    public static final Config<CommonConfig> HANDLE = ConfigManager.register(ConfigType.COMMON, "extended_drawers", new CommonConfig(64, 10, true, 16 * 64, false, true));
}
