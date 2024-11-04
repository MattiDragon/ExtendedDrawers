package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends WorldMixin {
    @Shadow public abstract ServerRecipeManager getRecipeManager();

    @Override
    public CompressionRecipeManager extended_drawers$getCompactingManager() {
        return ((CompressionRecipeManager.Provider) getRecipeManager()).extended_drawers$getCompactingManager();
    }
}
