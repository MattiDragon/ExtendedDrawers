package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;
import java.util.stream.StreamSupport;

@SuppressWarnings("UnstableApiUsage")
public class DrawerBlockEntityRenderer implements BlockEntityRenderer<DrawerBlockEntity> {
    public DrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }
    
    @Override
    public void render(DrawerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var dir = entity.getCachedState().get(DrawerBlock.FACING);
        var pos = dir.getUnitVector();
    
        matrices.translate(pos.getX() / 2 + 0.5, pos.getY() / 2 + 0.5, pos.getZ() / 2 + 0.5);
        matrices.multiply(dir.getRotationQuaternion());
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
        matrices.translate(0, 0, 0.01);
    
        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(entity.getWorld()), entity.getPos().offset(dir));
        var slots = ((DrawerBlock)entity.getCachedState().getBlock()).slots;
    
        switch (slots) {
            case 1 -> renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay);
            case 2 -> {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.5, 0, 0);
                renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[1], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay);
            }
            case 4 -> {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.5, 0.5, 0);
                renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[1], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay);
                matrices.translate(-1, -1, 0);
                renderSlot(entity.storages[2], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[3], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay);
            }
            default -> throw new IllegalStateException("unexpected drawer slot count");
        }
        
        matrices.pop();
    }
    
    private void renderSlot(DrawerBlockEntity.DrawerStorage storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, int overlay) {
        renderText(storage, light, matrices, vertexConsumers);
        if (storage.locked) renderLock(light, matrices, vertexConsumers, overlay);
        renderItem(storage, light, matrices, vertexConsumers, seed);
    }
    
    private void renderLock(int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay) {
        var mc = MinecraftClient.getInstance();
        
        // check for item
        if (mc.player != null && StreamSupport.stream(mc.player.getItemsHand().spliterator(), false).anyMatch(stack -> stack.isOf(ModItems.LOCK)))
            return;
        
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
        matrices.translate(-0.125, -0.24, -0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);
        var sprite = mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(ExtendedDrawers.id("item/lock"));
        var emitter = Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer()).meshBuilder().getEmitter();
        emitter.square(Direction.UP, 0, 0, 1, 1, 0);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        vertexConsumers.getBuffer(RenderLayer.getCutout()).quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), 1, 1, 1, light, overlay);
        matrices.pop();
    }
    
    private void renderItem(DrawerBlockEntity.DrawerStorage storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed) {
        matrices.push();
        matrices.scale(0.75f, 0.75f, 1);
        matrices.multiplyPositionMatrix(Matrix4f.scale(1, 1, 0.01f));
        MinecraftClient.getInstance().getItemRenderer().renderItem(storage.item.toStack(), ModelTransformation.Mode.GUI, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, seed);
        matrices.pop();
    }
    
    private void renderText(DrawerBlockEntity.DrawerStorage storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (storage.isResourceBlank()) return;
        
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.translate(0, 0.3, -0.01);
        matrices.scale(0.02f, 0.02f, 0.02f);
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var text = Long.toString(storage.amount);
        textRenderer.draw(text, -textRenderer.getWidth(text) / 2f, 0, 0xffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0x000000, light);
        matrices.pop();
    }
}
