package io.github.mattidragon.extendeddrawers.client.config.render;

import io.github.mattidragon.extendeddrawers.config.ConfigData;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.jspecify.annotations.Nullable;
import org.joml.Matrix3x2f;

public record LayoutPreviewRenderState(
        ConfigData config,
        float size,
        Matrix3x2f pose,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {

    public LayoutPreviewRenderState(ConfigData config, int size, Matrix3x2f pose, int x0, int y0, int x1, int y1, int scale, @Nullable ScreenRectangle scissorArea) {
        this(config, size, pose, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }
}
