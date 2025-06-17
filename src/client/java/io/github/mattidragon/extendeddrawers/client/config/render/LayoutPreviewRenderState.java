package io.github.mattidragon.extendeddrawers.client.config.render;

import io.github.mattidragon.extendeddrawers.config.ConfigData;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record LayoutPreviewRenderState(
        ConfigData config,
        float size,
        Matrix3x2f pose,
        int x1,
        int y1,
        int x2,
        int y2,
        float scale,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SpecialGuiElementRenderState {

    public LayoutPreviewRenderState(ConfigData config, int size, Matrix3x2f pose, int x1, int y1, int x2, int y2, int scale, ScreenRect scissorArea) {
        this(config, size, pose, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
    }
}
