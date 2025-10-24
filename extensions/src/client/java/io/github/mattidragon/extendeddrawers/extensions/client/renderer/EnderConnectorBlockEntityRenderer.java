package io.github.mattidragon.extendeddrawers.extensions.client.renderer;

import io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.EnderConnectorBlockEntity;
import io.github.mattidragon.extendeddrawers.extensions.client.renderer.state.EnderConnectorRenderState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class EnderConnectorBlockEntityRenderer implements BlockEntityRenderer<EnderConnectorBlockEntity, EnderConnectorRenderState> {
    private static final Identifier TEXTURE_ID = ExtendedDrawersExtensions.id("textures/entity/ender_connector_beam.png");

    public EnderConnectorBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public EnderConnectorRenderState createRenderState() {
        return new EnderConnectorRenderState();
    }

    @Override
    public void updateRenderState(EnderConnectorBlockEntity entity, EnderConnectorRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        state.rays = entity.rayDirectionCache();
    }

    @Override
    public void render(EnderConnectorRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        
        var light = state.lightmapCoordinates;

        for (var ray : state.rays) {
            var length = ray.length();
            var yaw = (float) Math.atan2(ray.x(), ray.z());
            var pitch = (float) Math.asin(ray.y() / length);

            // Anti-z-fighting
            matrices.translate(0.001, 0.001, 0.001);

            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(-pitch));

            queue.submitCustom(matrices, RenderLayer.getEntityTranslucent(TEXTURE_ID), (matricesEntry, vertexConsumer) -> {
                quad(matricesEntry, vertexConsumer, light, length, -0.1f, 0.1f, 0.1f, 0.1f, new Vector3f(0, 1, 0));
                quad(matricesEntry, vertexConsumer, light, length, -0.1f, 0.1f, -0.1f, -0.1f, new Vector3f(0, -1, 0));
                quad(matricesEntry, vertexConsumer, light, length, -0.1f, -0.1f, -0.1f, 0.1f, new Vector3f(-1, 0, 0));
                quad(matricesEntry, vertexConsumer, light, length, 0.1f, 0.1f, -0.1f, 0.1f, new Vector3f(1, 0, 0));
            });

            matrices.pop();
        }

        matrices.pop();
    }

    private static void quad(MatrixStack.Entry matricesEntry, VertexConsumer vertexConsumer, int light, float length, float x1, float x2, float y1, float y2, Vector3f normal) {
        var lengthFactor = length / 3f;
        vertexConsumer.vertex(matricesEntry, x1, y1, 0f)
                .color(0xffffffff).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV).light(light)
                .normal(matricesEntry, normal);
        vertexConsumer.vertex(matricesEntry, x2, y2, 0f)
                .color(0xffffffff).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV).light(light)
                .normal(matricesEntry, normal);
        vertexConsumer.vertex(matricesEntry, x2, y2, length)
                .color(0xffffffff).texture(1 * lengthFactor, 1).overlay(OverlayTexture.DEFAULT_UV).light(light)
                .normal(matricesEntry, normal);
        vertexConsumer.vertex(matricesEntry, x1, y1, length)
                .color(0xffffffff).texture(1 * lengthFactor, 0).overlay(OverlayTexture.DEFAULT_UV).light(light)
                .normal(matricesEntry, normal);
    }
}
