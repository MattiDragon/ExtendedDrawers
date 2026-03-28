package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends LevelMixin {
    @Shadow public abstract RecipeManager recipeAccess();

    @Override
    public CompressionRecipeManager extended_drawers$getCompactingManager() {
        return ((CompressionRecipeManager.Provider) recipeAccess()).extended_drawers$getCompactingManager();
    }
}
