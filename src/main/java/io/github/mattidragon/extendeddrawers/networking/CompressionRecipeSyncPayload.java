package io.github.mattidragon.extendeddrawers.networking;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.compacting.CompressionLadder;
import io.github.mattidragon.extendeddrawers.compacting.ServerCompressionRecipeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record CompressionRecipeSyncPayload(List<CompressionLadder> recipes, boolean clearRecipes) implements CustomPacketPayload {
    public static final Type<CompressionRecipeSyncPayload> ID = new Type<>(ExtendedDrawers.id("compression_recipe_sync"));
    private static final StreamCodec<RegistryFriendlyByteBuf, CompressionRecipeSyncPayload> CODEC = StreamCodec.composite(
            CompressionLadder.PACKET_CODEC.apply(ByteBufCodecs.list()), CompressionRecipeSyncPayload::recipes,
            ByteBufCodecs.BOOL, CompressionRecipeSyncPayload::clearRecipes,
            CompressionRecipeSyncPayload::new
    );

    public static void register() {
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            var server = player.level().getServer();
            ServerPlayNetworking.send(player, new CompressionRecipeSyncPayload(List.copyOf(ServerCompressionRecipeManager.of(server.getRecipeManager()).getLadders()), true));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
