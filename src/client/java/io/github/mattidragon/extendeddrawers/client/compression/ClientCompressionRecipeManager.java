package io.github.mattidragon.extendeddrawers.client.compression;

import io.github.mattidragon.extendeddrawers.compacting.CompressionLadder;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientCompressionRecipeManager implements CompressionRecipeManager {
    private final Map<ItemVariant, CompressionLadder> ladders = new HashMap<>();

    public static ClientCompressionRecipeManager of(ClientPlayNetworkHandler handler) {
        return ((Provider) handler).extended_drawers$getCompactingManager();
    }

    public void addLadders(List<CompressionLadder> recipes) {
        for (var recipe : recipes) {
            recipe.steps().forEach(step -> ladders.put(step.item(), recipe));
        }
    }

    @Override
    public CompressionLadder getLadder(ItemVariant item, World world) {
        if (ladders.containsKey(item)) {
            return ladders.get(item);
        }
        return new CompressionLadder(List.of(new CompressionLadder.Step(item, 1)));
    }

    public interface Provider extends CompressionRecipeManager.Provider {
        ClientCompressionRecipeManager extended_drawers$getCompactingManager();
    }
}
