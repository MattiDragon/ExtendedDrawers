package io.github.mattidragon.extendeddrawers.network.cache;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.Supplier;

public enum CachingMode implements StringRepresentable {
    NONE(NoOpNetworkStorageCache::new),
    SIMPLE(SimpleNetworkStorageCache::new),
    SMART(SmartNetworkStorageCache::new);

    public static final Codec<CachingMode> CODEC = StringRepresentable.fromEnum(CachingMode::values);

    private final Supplier<NetworkStorageCache> cacheSupplier;

    CachingMode(Supplier<NetworkStorageCache> cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }

    public NetworkStorageCache createCache() {
        return cacheSupplier.get();
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public Component getDisplayName() {
        return Component.translatable("config.extended_drawers.cachingMode." + getSerializedName());
    }
}
