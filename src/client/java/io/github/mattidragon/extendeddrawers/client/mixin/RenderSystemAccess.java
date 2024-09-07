package io.github.mattidragon.extendeddrawers.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderSystem.class)
public interface RenderSystemAccess {
    @Accessor(remap = false)
    static Vector3f[] getShaderLightDirections() {
        throw new AssertionError();
    }
}
