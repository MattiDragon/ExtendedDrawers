package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.configloader.api.DefaultedFieldCodec;
import io.github.mattidragon.configloader.api.GenerateMutable;

@GenerateMutable
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
            DefaultedFieldCodec.of(Codec.LONG, "drawerCapacity", DEFAULT.drawerCapacity).forGetter(StorageCategory::drawerCapacity),
            DefaultedFieldCodec.of(Codec.LONG, "compactingCapacity", DEFAULT.compactingCapacity).forGetter(StorageCategory::compactingCapacity),
            DefaultedFieldCodec.of(Codec.BOOL, "stackSizeAffectsCapacity", DEFAULT.stackSizeAffectsCapacity).forGetter(StorageCategory::stackSizeAffectsCapacity),
            DefaultedFieldCodec.of(Codec.BOOL, "slotCountAffectsCapacity", DEFAULT.slotCountAffectsCapacity).forGetter(StorageCategory::slotCountAffectsCapacity),
            DefaultedFieldCodec.of(Codec.INT, "t1UpgradeMultiplier", DEFAULT.t1UpgradeMultiplier).forGetter(StorageCategory::t1UpgradeMultiplier),
            DefaultedFieldCodec.of(Codec.INT, "t2UpgradeMultiplier", DEFAULT.t2UpgradeMultiplier).forGetter(StorageCategory::t2UpgradeMultiplier),
            DefaultedFieldCodec.of(Codec.INT, "t3UpgradeMultiplier", DEFAULT.t3UpgradeMultiplier).forGetter(StorageCategory::t3UpgradeMultiplier),
            DefaultedFieldCodec.of(Codec.INT, "t4UpgradeMultiplier", DEFAULT.t4UpgradeMultiplier).forGetter(StorageCategory::t4UpgradeMultiplier)
    ).apply(instance, StorageCategory::new));

    public MutableStorageCategory toMutable() {
        return new MutableStorageCategory(this);
    }
}
