package io.github.mattidragon.extendeddrawers.config.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.configloader.api.DefaultedFieldCodec;
import io.github.mattidragon.configloader.api.GenerateMutable;
import io.github.mattidragon.extendeddrawers.misc.CreativeBreakingBehaviour;

@GenerateMutable
public record MiscCategory(int insertAllTime,
                           CreativeBreakingBehaviour frontBreakingBehaviour,
                           CreativeBreakingBehaviour sideBreakingBehaviour,
                           boolean blockUpgradeRemovalsWithOverflow,
                           boolean allowRecursion,
                           boolean drawersDropContentsOnBreak) implements MutableMiscCategory.Source {
    public static final MiscCategory DEFAULT = new MiscCategory(10, CreativeBreakingBehaviour.MINE, CreativeBreakingBehaviour.BREAK, true, false, false);

    public static final Codec<MiscCategory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DefaultedFieldCodec.of(Codec.INT, "insertAllTime", DEFAULT.insertAllTime).forGetter(MiscCategory::insertAllTime),
            DefaultedFieldCodec.of(CreativeBreakingBehaviour.CODEC, "frontBreakingBehaviour", DEFAULT.frontBreakingBehaviour).forGetter(MiscCategory::frontBreakingBehaviour),
            DefaultedFieldCodec.of(CreativeBreakingBehaviour.CODEC, "sideBreakingBehaviour", DEFAULT.sideBreakingBehaviour).forGetter(MiscCategory::sideBreakingBehaviour),
            DefaultedFieldCodec.of(Codec.BOOL, "blockUpgradeRemovalsWithOverflow", DEFAULT.blockUpgradeRemovalsWithOverflow).forGetter(MiscCategory::blockUpgradeRemovalsWithOverflow),
            DefaultedFieldCodec.of(Codec.BOOL, "allowRecursion", DEFAULT.allowRecursion).forGetter(MiscCategory::allowRecursion),
            DefaultedFieldCodec.of(Codec.BOOL, "drawersDropContentsOnBreak", DEFAULT.drawersDropContentsOnBreak).forGetter(MiscCategory::drawersDropContentsOnBreak)
    ).apply(instance, MiscCategory::new));
}
