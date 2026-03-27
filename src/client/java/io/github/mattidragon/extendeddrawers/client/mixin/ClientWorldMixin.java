package io.github.mattidragon.extendeddrawers.client.mixin;

import io.github.mattidragon.extendeddrawers.client.compression.ClientCompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.mixin.WorldMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Mixin(ClientLevel.class)
public class ClientWorldMixin extends WorldMixin {
    @Override
    public CompressionRecipeManager extended_drawers$getCompactingManager() {
        return ((ClientCompressionRecipeManager.Provider) Objects.requireNonNull(Minecraft.getInstance().getConnection(), "network handler should exist"))
                .extended_drawers$getCompactingManager();
    }
}
