package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import static io.github.mattidragon.extendeddrawers.config.ConfigData.defaultingFieldOf;

public record StorageCategory(long drawerCapacity,
                              long compactingCapacity,
                              boolean stackSizeAffectsCapacity,
                              boolean slotCountAffectsCapacity,
                              int t1UpgradeMultiplier,
                              int t2UpgradeMultiplier,
                              int t3UpgradeMultiplier,
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
            defaultingFieldOf(Codec.LONG, "drawerCapacity", DEFAULT.drawerCapacity).forGetter(StorageCategory::drawerCapacity),
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
            this.defaultCapacity = values.drawerCapacity;
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
