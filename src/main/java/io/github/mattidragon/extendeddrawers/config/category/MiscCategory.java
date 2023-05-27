package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.extendeddrawers.misc.CreativeBreakingBehaviour;

import static io.github.mattidragon.extendeddrawers.config.ConfigData.defaultingFieldOf;

public record MiscCategory(int insertAllTime,
                           CreativeBreakingBehaviour frontBreakingBehaviour,
                           CreativeBreakingBehaviour sideBreakingBehaviour,
                           boolean blockUpgradeRemovalsWithOverflow,
                           boolean allowRecursion,
                           boolean drawersDropContentsOnBreak) {
    public static final MiscCategory DEFAULT = new MiscCategory(10, CreativeBreakingBehaviour.MINE, CreativeBreakingBehaviour.BREAK, true, false, false);

    public static final Codec<MiscCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            defaultingFieldOf(Codec.INT, "insertAllTime", DEFAULT.insertAllTime).forGetter(MiscCategory::insertAllTime),
            defaultingFieldOf(CreativeBreakingBehaviour.CODEC, "frontBreakingBehaviour", DEFAULT.frontBreakingBehaviour).forGetter(MiscCategory::frontBreakingBehaviour),
            defaultingFieldOf(CreativeBreakingBehaviour.CODEC, "sideBreakingBehaviour", DEFAULT.sideBreakingBehaviour).forGetter(MiscCategory::sideBreakingBehaviour),
            defaultingFieldOf(Codec.BOOL, "blockUpgradeRemovalsWithOverflow", DEFAULT.blockUpgradeRemovalsWithOverflow).forGetter(MiscCategory::blockUpgradeRemovalsWithOverflow),
            defaultingFieldOf(Codec.BOOL, "allowRecursion", DEFAULT.allowRecursion).forGetter(MiscCategory::allowRecursion),
            defaultingFieldOf(Codec.BOOL, "drawersDropContentsOnBreak", DEFAULT.drawersDropContentsOnBreak).forGetter(MiscCategory::drawersDropContentsOnBreak)
    ).apply(instance, MiscCategory::new));

    public Mutable toMutable() {
        return new Mutable(this);
    }

    public static final class Mutable {
        public int insertAllTime;
        public CreativeBreakingBehaviour frontBreakingBehaviour;
        public CreativeBreakingBehaviour sideBreakingBehaviour;
        public boolean blockUpgradeRemovalsWithOverflow;
        public boolean allowRecursion;
        public boolean drawersDropContentsOnBreak;

        private Mutable(MiscCategory values) {
            this.insertAllTime = values.insertAllTime;
            this.frontBreakingBehaviour = values.frontBreakingBehaviour;
            this.sideBreakingBehaviour = values.sideBreakingBehaviour;
            this.blockUpgradeRemovalsWithOverflow = values.blockUpgradeRemovalsWithOverflow;
            this.allowRecursion = values.allowRecursion;
            this.drawersDropContentsOnBreak = values.drawersDropContentsOnBreak;
        }

        public MiscCategory toImmutable() {
            return new MiscCategory(insertAllTime, frontBreakingBehaviour, sideBreakingBehaviour, blockUpgradeRemovalsWithOverflow, allowRecursion, drawersDropContentsOnBreak);
        }
    }
}
