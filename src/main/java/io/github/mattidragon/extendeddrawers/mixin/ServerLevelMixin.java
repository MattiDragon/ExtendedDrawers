package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.compacting.ServerCompressionRecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends LevelMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Override
    public CompressionRecipeManager extended_drawers$getCompactingManager() {
        return ServerCompressionRecipeManager.of(server);
    }
}
