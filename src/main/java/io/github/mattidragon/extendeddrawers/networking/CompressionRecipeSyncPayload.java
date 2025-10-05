package io.github.mattidragon.extendeddrawers.networking;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.compacting.CompressionLadder;
import io.github.mattidragon.extendeddrawers.compacting.ServerCompressionRecipeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record CompressionRecipeSyncPayload(List<CompressionLadder> recipes, boolean clearRecipes) implements CustomPayload {
    public static final Id<CompressionRecipeSyncPayload> ID = new Id<>(ExtendedDrawers.id("compression_recipe_sync"));
    private static final PacketCodec<RegistryByteBuf, CompressionRecipeSyncPayload> CODEC = PacketCodec.tuple(
            CompressionLadder.PACKET_CODEC.collect(PacketCodecs.toList()), CompressionRecipeSyncPayload::recipes,
            PacketCodecs.BOOLEAN, CompressionRecipeSyncPayload::clearRecipes,
            CompressionRecipeSyncPayload::new
    );

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            var server = player.getEntityWorld().getServer();
            ServerPlayNetworking.send(player, new CompressionRecipeSyncPayload(List.copyOf(ServerCompressionRecipeManager.of(server.getRecipeManager()).getLadders()), true));
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
