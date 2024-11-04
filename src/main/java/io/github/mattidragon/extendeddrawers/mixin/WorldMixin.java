package io.github.mattidragon.extendeddrawers.mixin;

import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class WorldMixin implements CompressionRecipeManager.Provider {
}
