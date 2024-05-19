package io.github.mattidragon.extendeddrawers.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.mattidragon.configloader.api.AlwaysSerializedOptionalFieldCodec;
import io.github.mattidragon.extendeddrawers.config.category.ClientCategory;
import io.github.mattidragon.extendeddrawers.config.category.MiscCategory;
import io.github.mattidragon.extendeddrawers.config.category.StorageCategory;

public record ConfigData(ClientCategory client, StorageCategory storage, MiscCategory misc) {
    public static final ConfigData DEFAULT = new ConfigData(ClientCategory.DEFAULT, StorageCategory.DEFAULT, MiscCategory.DEFAULT);

    public static final Codec<ConfigData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AlwaysSerializedOptionalFieldCodec.create(ClientCategory.CODEC, "client", DEFAULT.client).forGetter(ConfigData::client),
            AlwaysSerializedOptionalFieldCodec.create(StorageCategory.CODEC, "storage", DEFAULT.storage).forGetter(ConfigData::storage),
            AlwaysSerializedOptionalFieldCodec.create(MiscCategory.CODEC, "misc", DEFAULT.misc).forGetter(ConfigData::misc)
    ).apply(instance, ConfigData::new));
}
