package io.github.mattidragon.extendeddrawers.networking;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SetLimiterLimitPayload(int slot, long limit) implements CustomPayload {
    public static final Id<SetLimiterLimitPayload> ID = new Id<>(ExtendedDrawers.id("set_limiter_limit"));
    private static final PacketCodec<PacketByteBuf, SetLimiterLimitPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, SetLimiterLimitPayload::slot,
            PacketCodecs.VAR_LONG, SetLimiterLimitPayload::limit,
            SetLimiterLimitPayload::new
    );

    public SetLimiterLimitPayload {
        if (limit <= 0 && limit != -1) throw new IllegalArgumentException("Limiter limit must be above 0");
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID, (packet, context) -> {
            var slot = packet.slot;
            var player = context.player();
            if (!PlayerInventory.isValidHotbarIndex(slot) && slot != 40) return;
            var stack = player.getInventory().getStack(slot);
            if (!stack.isOf(ModItems.LIMITER)) return;
            if (packet.limit == -1) {
                stack.remove(ModDataComponents.LIMITER_LIMIT);
            } else {
                stack.set(ModDataComponents.LIMITER_LIMIT, new LimiterLimitComponent(packet.limit));
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
