package io.github.mattidragon.extendeddrawers.mixin.patchouli;

import net.fabricmc.loader.api.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "vazkii.patchouli.fabric.xplat.FabricXplatModContainer", remap = false)
public interface FabricXplatModContainerAccess {
    @Accessor(remap = false)
    ModContainer getContainer();
}
