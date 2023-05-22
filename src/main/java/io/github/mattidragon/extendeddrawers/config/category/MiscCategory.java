package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.extendeddrawers.misc.CreativeExtractionBehaviour;
import io.github.mattidragon.mconfig.config.Comment;

import static io.github.mattidragon.extendeddrawers.config.ConfigData.defaultingFieldOf;

public record MiscCategory(@Comment("The max time between the clicks of a double insert")
                           int insertAllTime,
                           @Comment("""
                                   How extraction in creative should be handled. Possible values:
                                   - 'normal': Vanilla instant creative breaking
                                   - 'front_mine': The front face is mined like in survival. The rest break instantly
                                   - 'all_mine': Every face can be mined like in survival
                                   - 'front_no_break': The block can't be broken from the front
                                   - 'all_no_break': The block can't be broken at all""")
                           CreativeExtractionBehaviour creativeExtractionMode,
                           @Comment("If enabled you can't remove upgrades if the slot would overflow after removal")
                           boolean blockUpgradeRemovalsWithOverflow,
                           @Comment("Allows you to place drawers insider shulker boxes and other drawers. Deeply nested storage can lead to chunk and player data corruption.")
                           boolean allowRecursion,
                           @Comment("If enabled drawer drop their contents instead of retaining them. Can cause lag.")
                           boolean drawersDropContentsOnBreak) {
    public static final MiscCategory DEFAULT = new MiscCategory(10, CreativeExtractionBehaviour.FRONT_NO_BREAK, true, false, false);

    public static final Codec<MiscCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            defaultingFieldOf(Codec.INT, "insertAllTime", DEFAULT.insertAllTime).forGetter(MiscCategory::insertAllTime),
            defaultingFieldOf(CreativeExtractionBehaviour.CODEC, "creativeExtractionMode", DEFAULT.creativeExtractionMode).forGetter(MiscCategory::creativeExtractionMode),
            defaultingFieldOf(Codec.BOOL, "blockUpgradeRemovalsWithOverflow", DEFAULT.blockUpgradeRemovalsWithOverflow).forGetter(MiscCategory::blockUpgradeRemovalsWithOverflow),
            defaultingFieldOf(Codec.BOOL, "allowRecursion", DEFAULT.allowRecursion).forGetter(MiscCategory::allowRecursion),
            defaultingFieldOf(Codec.BOOL, "drawersDropContentsOnBreak", DEFAULT.drawersDropContentsOnBreak).forGetter(MiscCategory::drawersDropContentsOnBreak)
    ).apply(instance, MiscCategory::new));

    public Mutable toMutable() {
        return new Mutable(this);
    }

    public static final class Mutable {
        public int insertAllTime;
        public CreativeExtractionBehaviour creativeExtractionMode;
        public boolean blockUpgradeRemovalsWithOverflow;
        public boolean allowRecursion;
        public boolean drawersDropContentsOnBreak;

        private Mutable(MiscCategory values) {
            this.insertAllTime = values.insertAllTime;
            this.creativeExtractionMode = values.creativeExtractionMode;
            this.blockUpgradeRemovalsWithOverflow = values.blockUpgradeRemovalsWithOverflow;
            this.allowRecursion = values.allowRecursion;
            this.drawersDropContentsOnBreak = values.drawersDropContentsOnBreak;
        }

        public MiscCategory toImmutable() {
            return new MiscCategory(insertAllTime, creativeExtractionMode, blockUpgradeRemovalsWithOverflow, allowRecursion, drawersDropContentsOnBreak);
        }
    }
}
