package io.github.mattidragon.extendeddrawers.client.mixin;

import io.github.mattidragon.extendeddrawers.client.compression.ClientCompressionRecipeManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin implements ClientCompressionRecipeManager.Provider {
    @Unique
    private final ClientCompressionRecipeManager compactingManager = new ClientCompressionRecipeManager();

    @Override
    public ClientCompressionRecipeManager extended_drawers$getCompactingManager() {
        return compactingManager;
    }
}
