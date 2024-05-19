package io.github.mattidragon.extendeddrawers.networking;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.compacting.CompressionLadder;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record CompressionOverrideSyncPayload(List<CompressionLadder> overrides) implements CustomPayload {
    public static final Id<CompressionOverrideSyncPayload> ID = new Id<>(ExtendedDrawers.id("compression_override_sync"));
    private static final PacketCodec<RegistryByteBuf, CompressionOverrideSyncPayload> CODEC = PacketCodec.tuple(
            CompressionLadder.PACKET_CODEC.collect(PacketCodecs.toList()), CompressionOverrideSyncPayload::overrides,
            CompressionOverrideSyncPayload::new
    );

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
                ServerPlayNetworking.send(player, new CompressionOverrideSyncPayload(CompressionRecipeManager.of(player.server.getRecipeManager()).getOverrides())));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
