package io.github.mattidragon.extendeddrawers.networking;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.compacting.CompressionLadder;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.NoSuchElementException;

@SuppressWarnings("UnstableApiUsage")
public class CompressionOverrideSyncPacket {
    public static final Identifier ID = ExtendedDrawers.id("compression_override_sync");

    private CompressionOverrideSyncPacket() {
    }

    public static void register() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> player.networkHandler.sendPacket(write(CompressionRecipeManager.of(player.server.getRecipeManager()).getOverrides())));
    }

    public static Packet<ClientPlayPacketListener> write(List<CompressionLadder> overrides) {
        var buf = PacketByteBufs.create();
        buf.writeCollection(overrides, (buf1, ladder) -> buf1.writeCollection(ladder.steps(), (buf2, step) -> {
            buf2.writeRegistryValue(Registry.ITEM, step.item().getItem());
            buf2.writeNbt(step.item().getNbt());
            buf2.writeVarInt(step.size());
        }));
        return ServerPlayNetworking.createS2CPacket(ID, buf);
    }

    public static List<CompressionLadder> read(PacketByteBuf buf) {
        return buf.readList(buf1 -> {
            var ladder = new CompressionLadder(buf1.readList(buf2 -> {
                var item = buf2.readRegistryValue(Registry.ITEM);
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
        });
    }
}
