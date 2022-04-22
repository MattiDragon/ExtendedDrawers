package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.block.ShadowDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

public class ShadowDrawerBlockEntityRenderer implements BlockEntityRenderer<ShadowDrawerBlockEntity> {
    public ShadowDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}
    
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void render(ShadowDrawerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var dir = entity.getCachedState().get(ShadowDrawerBlock.FACING);
        var pos = dir.getUnitVector();
    
        matrices.translate(pos.getX() / 2 + 0.5, pos.getY() / 2 + 0.5, pos.getZ() / 2 + 0.5);
        matrices.multiply(dir.getRotationQuaternion());
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
        matrices.translate(0, 0, 0.01);
    
        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(entity.getWorld()), entity.getPos().offset(dir));
        
        matrices.push();
        matrices.scale(0.75f, 0.75f, 1);
        matrices.multiplyPositionMatrix(Matrix4f.scale(1, 1, 0.01f));
        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.item.toStack(), ModelTransformation.Mode.GUI, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, (int)entity.getPos().asLong());
        matrices.pop();
    
        if (!entity.item.isBlank()) {
            var amount = entity.createStorage().simulateExtract(entity.item, Long.MAX_VALUE, null);
            
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
            matrices.translate(0, 0.3, -0.01);
            matrices.scale(0.02f, 0.02f, 0.02f);
            var textRenderer = MinecraftClient.getInstance().textRenderer;
            
            var text = Long.toString(amount);
            textRenderer.draw(text, -textRenderer.getWidth(text) / 2f, 0, 0xffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0x000000, light);
            matrices.pop();
        }
        matrices.pop();
    }
}
