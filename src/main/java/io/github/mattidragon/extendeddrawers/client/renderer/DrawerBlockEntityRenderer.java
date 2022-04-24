package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.config.ClientConfig;
import io.github.mattidragon.extendeddrawers.drawer.DrawerSlot;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.Objects;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

@SuppressWarnings("UnstableApiUsage")
public class DrawerBlockEntityRenderer implements BlockEntityRenderer<DrawerBlockEntity> {
    public DrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}
    
    @Override
    public int getRenderDistance() {
        var config = ClientConfig.HANDLE.get();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
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
        var blockPos = entity.getPos();
        
        switch (slots) {
            case 1 -> renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
            case 2 -> {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.5, 0, 0);
                renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[1], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
            }
            case 4 -> {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.5, 0.5, 0);
                renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[1], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
                matrices.translate(-1, -1, 0);
                renderSlot(entity.storages[2], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[3], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
            }
            default -> throw new IllegalStateException("unexpected drawer slot count");
        }
        
        matrices.pop();
    }
    
    private void renderSlot(DrawerSlot storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, int overlay, BlockPos pos) {
        //noinspection ConstantConditions
        var playerPos = MinecraftClient.getInstance().player.getPos();
        var config = ClientConfig.HANDLE.get();
        
        if (pos.isWithinDistance(playerPos, config.textRenderDistance()))
            renderText(storage, light, matrices, vertexConsumers);
        if (pos.isWithinDistance(playerPos, config.iconRenderDistance()))
            renderIcons(storage, light, matrices, vertexConsumers, overlay);
        if (pos.isWithinDistance(playerPos, config.itemRenderDistance()))
            renderItem(storage, light, matrices, vertexConsumers, seed);
    }
    
    private void renderIcons(DrawerSlot storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay) {
        var icons = new ArrayList<Sprite>();
        var mc = MinecraftClient.getInstance();
        var blockAtlas = mc.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        if (storage.locked)
            icons.add(blockAtlas.apply(id("item/lock")));
        
        if (storage.upgrade != null)
            icons.add(blockAtlas.apply(storage.upgrade.sprite));

        
        var increment = 1.0 / (icons.size() + 1.0);
        matrices.push();
        matrices.translate(-0.5, 0, 0);
        for (var icon : icons) {
            matrices.translate(increment, 0, 0);
            renderIcon(icon, light, matrices, vertexConsumers, overlay);
        }
        matrices.pop();
    }
    
    private void renderIcon(Sprite sprite, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay) {
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
        matrices.translate(-0.125, -0.24, -0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);
        var emitter = Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer()).meshBuilder().getEmitter();
        emitter.square(Direction.UP, 0, 0, 1, 1, 0);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        vertexConsumers.getBuffer(RenderLayer.getCutout()).quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), 1, 1, 1, light, overlay);
        matrices.pop();
    }
    
    private void renderItem(DrawerSlot storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed) {
        matrices.push();
        matrices.scale(0.75f, 0.75f, 1);
        matrices.multiplyPositionMatrix(Matrix4f.scale(1, 1, 0.01f));
        MinecraftClient.getInstance().getItemRenderer().renderItem(storage.item.toStack(), ModelTransformation.Mode.GUI, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, seed);
        matrices.pop();
    }
    
    private void renderText(DrawerSlot storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
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
