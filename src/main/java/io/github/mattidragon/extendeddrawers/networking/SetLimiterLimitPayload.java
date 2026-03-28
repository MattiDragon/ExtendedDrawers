package io.github.mattidragon.extendeddrawers.networking;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Inventory;

public record SetLimiterLimitPayload(int slot, long limit) implements CustomPacketPayload {
    public static final Type<SetLimiterLimitPayload> ID = new Type<>(ExtendedDrawers.id("set_limiter_limit"));
    private static final StreamCodec<FriendlyByteBuf, SetLimiterLimitPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetLimiterLimitPayload::slot,
            ByteBufCodecs.VAR_LONG, SetLimiterLimitPayload::limit,
            SetLimiterLimitPayload::new
    );

    public SetLimiterLimitPayload {
        if (limit <= 0 && limit != -1) throw new IllegalArgumentException("Limiter limit must be above 0");
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(ID, CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID, (packet, context) -> {
            var slot = packet.slot;
            var player = context.player();
            if (!Inventory.isHotbarSlot(slot) && slot != 40) return;
            var stack = player.getInventory().getItem(slot);
            if (!stack.is(ModItems.LIMITER)) return;
            if (packet.limit == -1) {
                stack.remove(ModDataComponents.LIMITER_LIMIT);
            } else {
                stack.set(ModDataComponents.LIMITER_LIMIT, new LimiterLimitComponent(packet.limit));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
