package io.github.mattidragon.extendeddrawers.config;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.configloader.api.DefaultedFieldCodec;
import io.github.mattidragon.extendeddrawers.config.category.ClientCategory;
import io.github.mattidragon.extendeddrawers.config.category.MiscCategory;
import io.github.mattidragon.extendeddrawers.config.category.StorageCategory;

public record ConfigData(ClientCategory client, StorageCategory storage, MiscCategory misc) {
    public static final ConfigData DEFAULT = new ConfigData(ClientCategory.DEFAULT, StorageCategory.DEFAULT, MiscCategory.DEFAULT);

    public static final Codec<ConfigData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DefaultedFieldCodec.of(ClientCategory.CODEC, "client", DEFAULT.client).forGetter(ConfigData::client),
            DefaultedFieldCodec.of(StorageCategory.CODEC, "storage", DEFAULT.storage).forGetter(ConfigData::storage),
            DefaultedFieldCodec.of(MiscCategory.CODEC, "misc", DEFAULT.misc).forGetter(ConfigData::misc)
    ).apply(instance, ConfigData::new));
}
