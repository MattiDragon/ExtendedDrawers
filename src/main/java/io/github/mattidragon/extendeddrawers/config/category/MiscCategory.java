package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.configloader.api.AlwaysSerializedOptionalFieldCodec;
import io.github.mattidragon.configloader.api.GenerateMutable;
import io.github.mattidragon.extendeddrawers.misc.CreativeBreakingBehaviour;
import io.github.mattidragon.extendeddrawers.network.cache.CachingMode;

@GenerateMutable
public record MiscCategory(int insertAllTime,
                           CreativeBreakingBehaviour frontBreakingBehaviour,
                           CreativeBreakingBehaviour sideBreakingBehaviour,
                           CachingMode cachingMode,
                           boolean blockUpgradeRemovalsWithOverflow,
                           boolean allowRecursion,
                           boolean drawersDropContentsOnBreak,
                           boolean dropDrawersInCreative) implements MutableMiscCategory.Source {
    public static final MiscCategory DEFAULT = new MiscCategory(10, CreativeBreakingBehaviour.MINE, CreativeBreakingBehaviour.BREAK, CachingMode.SIMPLE, true, false, false, true);

    public static final Codec<MiscCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AlwaysSerializedOptionalFieldCodec.create(Codec.INT, "insertAllTime", DEFAULT.insertAllTime).forGetter(MiscCategory::insertAllTime),
            AlwaysSerializedOptionalFieldCodec.create(CreativeBreakingBehaviour.CODEC, "frontBreakingBehaviour", DEFAULT.frontBreakingBehaviour).forGetter(MiscCategory::frontBreakingBehaviour),
            AlwaysSerializedOptionalFieldCodec.create(CreativeBreakingBehaviour.CODEC, "sideBreakingBehaviour", DEFAULT.sideBreakingBehaviour).forGetter(MiscCategory::sideBreakingBehaviour),
            AlwaysSerializedOptionalFieldCodec.create(CachingMode.CODEC, "cachingMode", DEFAULT.cachingMode).forGetter(MiscCategory::cachingMode),
            AlwaysSerializedOptionalFieldCodec.create(Codec.BOOL, "blockUpgradeRemovalsWithOverflow", DEFAULT.blockUpgradeRemovalsWithOverflow).forGetter(MiscCategory::blockUpgradeRemovalsWithOverflow),
            AlwaysSerializedOptionalFieldCodec.create(Codec.BOOL, "allowRecursion", DEFAULT.allowRecursion).forGetter(MiscCategory::allowRecursion),
            AlwaysSerializedOptionalFieldCodec.create(Codec.BOOL, "drawersDropContentsOnBreak", DEFAULT.drawersDropContentsOnBreak).forGetter(MiscCategory::drawersDropContentsOnBreak),
            AlwaysSerializedOptionalFieldCodec.create(Codec.BOOL, "dropDrawersInCreative", DEFAULT.dropDrawersInCreative).forGetter(MiscCategory::dropDrawersInCreative)
    ).apply(instance, MiscCategory::new));
}
