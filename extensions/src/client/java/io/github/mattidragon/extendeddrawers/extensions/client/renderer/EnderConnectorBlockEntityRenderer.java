package io.github.mattidragon.extendeddrawers.extensions.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.EnderConnectorBlockEntity;
import io.github.mattidragon.extendeddrawers.extensions.client.renderer.state.EnderConnectorRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class EnderConnectorBlockEntityRenderer implements BlockEntityRenderer<EnderConnectorBlockEntity, EnderConnectorRenderState> {
    private static final Identifier TEXTURE_ID = ExtendedDrawersExtensions.id("textures/entity/ender_connector_beam.png");

    public EnderConnectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public EnderConnectorRenderState createRenderState() {
        return new EnderConnectorRenderState();
    }

    @Override
    public void extractRenderState(EnderConnectorBlockEntity entity, EnderConnectorRenderState state, float tickProgress, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(entity, state, tickProgress, cameraPos, crumblingOverlay);
        state.rays = entity.rayDirectionCache();
    }

    @Override
    public void submit(EnderConnectorRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0.5, 0.5, 0.5);
        
        var light = state.lightCoords;

        for (var ray : state.rays) {
            var length = ray.length();
            var yaw = (float) Math.atan2(ray.x(), ray.z());
            var pitch = (float) Math.asin(ray.y() / length);

            // Anti-z-fighting
            matrices.translate(0.001, 0.001, 0.001);

            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotation(yaw));
            matrices.mulPose(Axis.XP.rotation(-pitch));

            queue.submitCustomGeometry(matrices, RenderTypes.entityTranslucent(TEXTURE_ID), (matricesEntry, vertexConsumer) -> {
                quad(matricesEntry, vertexConsumer, light, length, -0.1f, 0.1f, 0.1f, 0.1f, new Vector3f(0, 1, 0));
                quad(matricesEntry, vertexConsumer, light, length, -0.1f, 0.1f, -0.1f, -0.1f, new Vector3f(0, -1, 0));
                quad(matricesEntry, vertexConsumer, light, length, -0.1f, -0.1f, -0.1f, 0.1f, new Vector3f(-1, 0, 0));
                quad(matricesEntry, vertexConsumer, light, length, 0.1f, 0.1f, -0.1f, 0.1f, new Vector3f(1, 0, 0));
            });

            matrices.popPose();
        }

        matrices.popPose();
    }

    private static void quad(PoseStack.Pose matricesEntry, VertexConsumer vertexConsumer, int light, float length, float x1, float x2, float y1, float y2, Vector3f normal) {
        var lengthFactor = length / 3f;
        vertexConsumer.addVertex(matricesEntry, x1, y1, 0f)
                .setColor(0xffffffff).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(matricesEntry, normal);
        vertexConsumer.addVertex(matricesEntry, x2, y2, 0f)
                .setColor(0xffffffff).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(matricesEntry, normal);
        vertexConsumer.addVertex(matricesEntry, x2, y2, length)
                .setColor(0xffffffff).setUv(1 * lengthFactor, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(matricesEntry, normal);
        vertexConsumer.addVertex(matricesEntry, x1, y1, length)
                .setColor(0xffffffff).setUv(1 * lengthFactor, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(matricesEntry, normal);
    }
}
