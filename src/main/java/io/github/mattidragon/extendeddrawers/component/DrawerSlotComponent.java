package io.github.mattidragon.extendeddrawers.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record DrawerSlotComponent(
        ItemVariant upgrade,
        ItemVariant limiter,
        boolean locked,
        boolean hidden,
        boolean voiding,
        boolean duping,
        ItemVariant item,
        long amount
) {
    public static final Codec<DrawerSlotComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemVariant.CODEC.fieldOf("upgrade").forGetter(DrawerSlotComponent::upgrade),
            ItemVariant.CODEC.fieldOf("limiter").forGetter(DrawerSlotComponent::limiter),
            Codec.BOOL.fieldOf("locked").forGetter(DrawerSlotComponent::locked),
            Codec.BOOL.fieldOf("hidden").forGetter(DrawerSlotComponent::hidden),
            Codec.BOOL.fieldOf("voiding").forGetter(DrawerSlotComponent::voiding),
            Codec.BOOL.fieldOf("duping").forGetter(DrawerSlotComponent::duping),
            ItemVariant.CODEC.fieldOf("item").forGetter(DrawerSlotComponent::item),
            Codec.LONG.fieldOf("amount").forGetter(DrawerSlotComponent::amount)
    ).apply(instance, DrawerSlotComponent::new));
    public static final PacketCodec<RegistryByteBuf, DrawerSlotComponent> PACKET_CODEC = PacketCodecs.registryCodec(CODEC);
}