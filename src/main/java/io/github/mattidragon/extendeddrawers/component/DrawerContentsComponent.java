package io.github.mattidragon.extendeddrawers.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;

public record DrawerContentsComponent(List<DrawerSlotComponent> slots) {
    public static final Codec<DrawerContentsComponent> CODEC = DrawerSlotComponent.CODEC.listOf(1, 4).xmap(DrawerContentsComponent::new, DrawerContentsComponent::slots);
    public static final PacketCodec<RegistryByteBuf, DrawerContentsComponent> PACKET_CODEC = DrawerSlotComponent.PACKET_CODEC.collect(PacketCodecs.toList(4)).xmap(DrawerContentsComponent::new, DrawerContentsComponent::slots);

}
