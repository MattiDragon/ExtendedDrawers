package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.mconfig.config.Comment;

import static io.github.mattidragon.extendeddrawers.config.ConfigData.defaultingFieldOf;

public record StorageCategory(
                              @Comment("How many items drawers are able to hold")
                              long defaultCapacity,
                              @Comment("How many items compacting drawers are able to hold")
                              long compactingCapacity,
                              @Comment("Whether the stack size of the item should affect capacity")
                              boolean stackSizeAffectsCapacity,
                              @Comment("Whether the amount of slots on a drawers should affect capacity")
                              boolean slotCountAffectsCapacity,
                              @Comment("The multiplier the T1 upgrade applies to the capacity of drawers")
                              int t1UpgradeMultiplier,
                              @Comment("The multiplier the T2 upgrade applies to the capacity of drawers")
                              int t2UpgradeMultiplier,
                              @Comment("The multiplier the T3 upgrade applies to the capacity of drawers")
                              int t3UpgradeMultiplier,
                              @Comment("The multiplier the T4 upgrade applies to the capacity of drawers")
                              int t4UpgradeMultiplier) {
    public static final StorageCategory DEFAULT = new StorageCategory(
            16 * 64,
            16 * 64,
            false,
            true,
            2,
            4,
            8,
            16);

    public static final Codec<StorageCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            defaultingFieldOf(Codec.LONG, "defaultCapacity", DEFAULT.defaultCapacity).forGetter(StorageCategory::defaultCapacity),
            defaultingFieldOf(Codec.LONG, "compactingCapacity", DEFAULT.compactingCapacity).forGetter(StorageCategory::compactingCapacity),
            defaultingFieldOf(Codec.BOOL, "stackSizeAffectsCapacity", DEFAULT.stackSizeAffectsCapacity).forGetter(StorageCategory::stackSizeAffectsCapacity),
            defaultingFieldOf(Codec.BOOL, "slotCountAffectsCapacity", DEFAULT.slotCountAffectsCapacity).forGetter(StorageCategory::slotCountAffectsCapacity),
            defaultingFieldOf(Codec.INT, "t1UpgradeMultiplier", DEFAULT.t1UpgradeMultiplier).forGetter(StorageCategory::t1UpgradeMultiplier),
            defaultingFieldOf(Codec.INT, "t2UpgradeMultiplier", DEFAULT.t2UpgradeMultiplier).forGetter(StorageCategory::t2UpgradeMultiplier),
            defaultingFieldOf(Codec.INT, "t3UpgradeMultiplier", DEFAULT.t3UpgradeMultiplier).forGetter(StorageCategory::t3UpgradeMultiplier),
            defaultingFieldOf(Codec.INT, "t4UpgradeMultiplier", DEFAULT.t4UpgradeMultiplier).forGetter(StorageCategory::t4UpgradeMultiplier)
    ).apply(instance, StorageCategory::new));

    public Mutable toMutable() {
        return new Mutable(this);
    }

    public static final class Mutable {
        public long defaultCapacity;
        public long compactingCapacity;
        public boolean stackSizeAffectsCapacity;
        public boolean slotCountAffectsCapacity;
        public int t1UpgradeMultiplier;
        public int t2UpgradeMultiplier;
        public int t3UpgradeMultiplier;
        public int t4UpgradeMultiplier;

        private Mutable(StorageCategory values) {
            this.defaultCapacity = values.defaultCapacity;
            this.compactingCapacity = values.compactingCapacity;
            this.stackSizeAffectsCapacity = values.stackSizeAffectsCapacity;
            this.slotCountAffectsCapacity = values.slotCountAffectsCapacity;
            this.t1UpgradeMultiplier = values.t1UpgradeMultiplier;
            this.t2UpgradeMultiplier = values.t2UpgradeMultiplier;
            this.t3UpgradeMultiplier = values.t3UpgradeMultiplier;
            this.t4UpgradeMultiplier = values.t4UpgradeMultiplier;
        }

        public StorageCategory toImmutable() {
            return new StorageCategory(
                    defaultCapacity,
                    compactingCapacity,
                    stackSizeAffectsCapacity,
                    slotCountAffectsCapacity,
                    t1UpgradeMultiplier,
                    t2UpgradeMultiplier,
                    t3UpgradeMultiplier,
                    t4UpgradeMultiplier);
        }
    }
}
