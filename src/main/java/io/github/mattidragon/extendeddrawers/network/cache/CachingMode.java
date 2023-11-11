package io.github.mattidragon.extendeddrawers.network.cache;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;
import java.util.function.Supplier;

public enum CachingMode implements StringIdentifiable {
    NONE(NoOpNetworkStorageCache::new),
    SIMPLE(SimpleNetworkStorageCache::new),
    SMART(SmartNetworkStorageCache::new);

    public static final Codec<CachingMode> CODEC = StringIdentifiable.createCodec(CachingMode::values);

    private final Supplier<NetworkStorageCache> cacheSupplier;

    CachingMode(Supplier<NetworkStorageCache> cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }

    public NetworkStorageCache createCache() {
        return cacheSupplier.get();
    }

    @Override
    public String asString() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Text getDisplayName() {
        return Text.translatable("config.extended_drawers.cachingMode." + asString());
    }
}
