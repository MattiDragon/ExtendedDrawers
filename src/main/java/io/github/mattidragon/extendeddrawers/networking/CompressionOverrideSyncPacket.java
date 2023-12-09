package io.github.mattidragon.extendeddrawers.networking;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.compacting.CompressionLadder;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;

import java.util.List;
import java.util.NoSuchElementException;

public record CompressionOverrideSyncPacket(List<CompressionLadder> overrides) implements FabricPacket {
    public static final PacketType<CompressionOverrideSyncPacket> TYPE = PacketType.create(ExtendedDrawers.id("compression_override_sync"), CompressionOverrideSyncPacket::new);

    public CompressionOverrideSyncPacket(PacketByteBuf buf) {
        this(buf.readList(buf1 -> {
            var ladder = new CompressionLadder(buf1.readList(buf2 -> {
                var item = buf2.readRegistryValue(Registries.ITEM);
                if (item == null)
                    throw new NoSuchElementException("Invalid item in compression ladder deserialization");
                var nbt = buf2.readNbt();
                var size = buf2.readVarInt();
                return new CompressionLadder.Step(ItemVariant.of(item, nbt), size);
            }));
            if (ladder.steps().isEmpty()) throw new IllegalStateException("No steps in ladder");
            var compression = 0;
            for (var step : ladder.steps()) {
                if (compression >= step.size()) throw new IllegalStateException("Illegal ladder order");
                compression = step.size();
            }

            return ladder;
        }));
    }

    public static void register() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
                ServerPlayNetworking.send(player, new CompressionOverrideSyncPacket(CompressionRecipeManager.of(player.server.getRecipeManager()).getOverrides())));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeCollection(overrides, (buf1, ladder) -> buf1.writeCollection(ladder.steps(), (buf2, step) -> {
            buf2.writeRegistryValue(Registries.ITEM, step.item().getItem());
            buf2.writeNbt(step.item().getNbt());
            buf2.writeVarInt(step.size());
        }));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
