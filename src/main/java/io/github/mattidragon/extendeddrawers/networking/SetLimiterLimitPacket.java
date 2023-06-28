package io.github.mattidragon.extendeddrawers.networking;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

public record SetLimiterLimitPacket(int slot, long limit) implements FabricPacket {
    private static final PacketType<SetLimiterLimitPacket> TYPE = PacketType.create(ExtendedDrawers.id("set_limiter_limit"), SetLimiterLimitPacket::new);

    public SetLimiterLimitPacket {
        if (limit <= 0 && limit != -1) throw new IllegalArgumentException("Limiter limit must be above 0");
    }

    public SetLimiterLimitPacket(PacketByteBuf buf) {
        this(buf.readVarInt(), buf.readVarLong());
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(TYPE, (packet, player, responseSender) -> {
            var slot = packet.slot;
            if (!PlayerInventory.isValidHotbarIndex(slot) && slot != 40) return;
            var stack = player.getInventory().getStack(slot);
            if (!stack.isOf(ModItems.LIMITER)) return;
            if (packet.limit == -1) {
                var nbt = stack.getNbt();
                if (nbt == null) return;

                nbt.remove("limit");
                if (nbt.isEmpty()) {
                    stack.setNbt(null);
                }
            } else {
                stack.getOrCreateNbt().putLong("limit", packet.limit);
            }
        });
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(slot);
        buf.writeVarLong(limit);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
