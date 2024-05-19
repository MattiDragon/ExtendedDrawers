package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.configloader.api.AlwaysSerializedOptionalFieldCodec;
import io.github.mattidragon.configloader.api.GenerateMutable;

@GenerateMutable
public record StorageCategory(long drawerCapacity,
                              long compactingCapacity,
                              boolean stackSizeAffectsCapacity,
                              boolean slotCountAffectsCapacity,
                              int t1UpgradeMultiplier,
                              int t2UpgradeMultiplier,
                              int t3UpgradeMultiplier,
                              int t4UpgradeMultiplier) implements MutableStorageCategory.Source {
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
            AlwaysSerializedOptionalFieldCodec.create(Codec.LONG, "drawerCapacity", DEFAULT.drawerCapacity).forGetter(StorageCategory::drawerCapacity),
            AlwaysSerializedOptionalFieldCodec.create(Codec.LONG, "compactingCapacity", DEFAULT.compactingCapacity).forGetter(StorageCategory::compactingCapacity),
            AlwaysSerializedOptionalFieldCodec.create(Codec.BOOL, "stackSizeAffectsCapacity", DEFAULT.stackSizeAffectsCapacity).forGetter(StorageCategory::stackSizeAffectsCapacity),
            AlwaysSerializedOptionalFieldCodec.create(Codec.BOOL, "slotCountAffectsCapacity", DEFAULT.slotCountAffectsCapacity).forGetter(StorageCategory::slotCountAffectsCapacity),
            AlwaysSerializedOptionalFieldCodec.create(Codec.INT, "t1UpgradeMultiplier", DEFAULT.t1UpgradeMultiplier).forGetter(StorageCategory::t1UpgradeMultiplier),
            AlwaysSerializedOptionalFieldCodec.create(Codec.INT, "t2UpgradeMultiplier", DEFAULT.t2UpgradeMultiplier).forGetter(StorageCategory::t2UpgradeMultiplier),
            AlwaysSerializedOptionalFieldCodec.create(Codec.INT, "t3UpgradeMultiplier", DEFAULT.t3UpgradeMultiplier).forGetter(StorageCategory::t3UpgradeMultiplier),
            AlwaysSerializedOptionalFieldCodec.create(Codec.INT, "t4UpgradeMultiplier", DEFAULT.t4UpgradeMultiplier).forGetter(StorageCategory::t4UpgradeMultiplier)
    ).apply(instance, StorageCategory::new));
}
