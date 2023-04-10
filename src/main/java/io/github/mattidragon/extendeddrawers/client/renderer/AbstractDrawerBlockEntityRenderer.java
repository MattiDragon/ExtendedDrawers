package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.config.ClientConfig;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractDrawerBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    public AbstractDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {}
    
    public void renderSlot(ItemVariant item, @Nullable Long amount, List<Sprite> icons, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int seed, BlockPos pos, World world) {
        //noinspection ConstantConditions
        var playerPos = MinecraftClient.getInstance().player.getPos();
        var config = ClientConfig.HANDLE.get();
    
        if (pos.isWithinDistance(playerPos, config.textRenderDistance()) && amount != null)
            renderText(amount, light, matrices, vertexConsumers);
        if (pos.isWithinDistance(playerPos, config.iconRenderDistance()))
            renderIcons(icons, light, matrices, vertexConsumers, overlay);
        if (pos.isWithinDistance(playerPos, config.itemRenderDistance()))
            renderItem(item, light, matrices, vertexConsumers, world, seed);
    }
    
    protected final boolean shouldRender(T drawer, Direction facing) {
        var world = drawer.getWorld();
        if (world == null) return false;
        var pos = drawer.getPos();
        var state = drawer.getCachedState();
        
        return Block.shouldDrawSide(state, world, pos, facing, pos.offset(facing));
    }
    
    private void renderIcons(List<Sprite> icons, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay) {
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
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.translate(-0.125, -0.24, -0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);
        var emitter = Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer()).meshBuilder().getEmitter();
        emitter.square(Direction.UP, 0, 0, 1, 1, 0);
        emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
        vertexConsumers.getBuffer(RenderLayer.getCutout()).quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), 1, 1, 1, light, overlay);
        matrices.pop();
    }
    
    private void renderItem(ItemVariant item, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int seed) {
        var itemScale = ClientConfig.HANDLE.get().itemScale();

        matrices.push();
        matrices.scale(itemScale, itemScale, 1);
        matrices.scale(0.75f, 0.75f, 1);
        matrices.multiplyPositionMatrix(new Matrix4f().scale(1, 1, 0.01f));
        MinecraftClient.getInstance().getItemRenderer().renderItem(item.toStack(), ModelTransformationMode.GUI, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, seed);
        matrices.pop();
    }
    
    private void renderText(long amount, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var itemScale = ClientConfig.HANDLE.get().itemScale();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.translate(0, 0.3, -0.01);
        matrices.scale(itemScale, itemScale, 1);
        matrices.scale(0.02f, 0.02f, 0.02f);
        var textRenderer = MinecraftClient.getInstance().textRenderer;
        var text = Long.toString(amount);
        textRenderer.draw(text, -textRenderer.getWidth(text) / 2f, 0, 0xffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0x000000, light);
        matrices.pop();
    }
}
