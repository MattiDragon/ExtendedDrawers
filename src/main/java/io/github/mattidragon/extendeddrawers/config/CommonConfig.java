package io.github.mattidragon.extendeddrawers.config;

import io.github.mattidragon.extendeddrawers.misc.CreativeExtractionBehaviour;
import io.github.mattidragon.mconfig.config.*;

@NonReloadable
@Comment("This is the common config. It contains gameplay settings.\nIt isn't automatically synced, but it is recommended that the client has the same settings as the server\n ")
public record CommonConfig(
        @Comment("The max time between the clicks of a double insert")
        int insertAllTime,
        @Comment("How many items drawers are able to hold")
        long defaultCapacity,
        @Comment("How many items compacting drawers are able to hold")
        long compactingCapacity,
        @Comment("Whether the stack size of the item should affect capacity")
        boolean stackSizeAffectsCapacity,
        @Comment("Whether the amount of slots on a drawers should affect capacity")
        boolean slotCountAffectsCapacity,
        @Comment("""
                How extraction in creative should be handled. Possible values:
                 - 'normal': Vanilla instant creative breaking
                 - 'front_mine': The front face is mined like in survival. The rest break instantly
                 - 'all_mine': Every face can be mined like in survival
                 - 'front_no_break': The block can't be broken from the front
                 - 'all_no_break': The block can't be broken at all""")
        CreativeExtractionBehaviour creativeExtractionMode,
        @Comment("Whether to fix drawer networks on chunk load. Disable only if it causes issues")
        boolean automaticNetworkHealing,
        @Comment("If enabled you can't remove upgrades if the slot would overflow after removal")
        boolean blockUpgradeRemovalsWithOverflow,
        @Comment("Allows you to place drawers insider shulker boxes and other drawers. Deeply nested storage can lead to chunk and player data corruption.")
        boolean allowRecursion,
        @Comment("If enabled drawer drop their contents instead of retaining them. Can cause lag.")
        boolean drawersDropContentsOnBreak,
        @Comment("The multiplier the T1 upgrade applies to the capacity of drawers")
        int t1UpgradeMultiplier,
        @Comment("The multiplier the T2 upgrade applies to the capacity of drawers")
        int t2UpgradeMultiplier,
        @Comment("The multiplier the T3 upgrade applies to the capacity of drawers")
        int t3UpgradeMultiplier,
        @Comment("The multiplier the T4 upgrade applies to the capacity of drawers")
        int t4UpgradeMultiplier) {
    public static final Config<CommonConfig> HANDLE = ConfigManager.register(ConfigType.COMMON,
            "extended_drawers",
            new CommonConfig(10,
                    16 * 64,
                    16 * 64,
                    false,
                    true,
                    CreativeExtractionBehaviour.FRONT_NO_BREAK,
                    true,
                    true,
                    false,
                    false,
                    2,
                    4,
                    8,
                    16));
}
