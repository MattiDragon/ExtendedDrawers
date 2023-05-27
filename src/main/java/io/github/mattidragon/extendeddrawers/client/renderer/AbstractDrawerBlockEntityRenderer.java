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
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public abstract class AbstractDrawerBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private static final Quaternion ITEM_LIGHT_ROTATION_3D;
    private static final Quaternion ITEM_LIGHT_ROTATION_FLAT;

    private final ItemRenderer itemRenderer;
    private final TextRenderer textRenderer;

    static {
        ITEM_LIGHT_ROTATION_3D = Vec3f.POSITIVE_X.getDegreesQuaternion(-15);
        ITEM_LIGHT_ROTATION_3D.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(15));
        ITEM_LIGHT_ROTATION_FLAT = Vec3f.POSITIVE_X.getDegreesQuaternion(-45);

    }

    public AbstractDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.textRenderer = context.getTextRenderer();
    }
    
    public void renderSlot(ItemVariant item, @Nullable Long amount, List<Sprite> icons, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int seed, BlockPos pos) {
        var player = MinecraftClient.getInstance().player;
        var playerPos = player == null ? Vec3d.ofCenter(pos) : player.getPos();
        var config = ClientConfig.HANDLE.get();
    
        if (pos.isWithinDistance(playerPos, config.textRenderDistance()) && amount != null)
            renderText(amount, light, matrices, vertexConsumers);
        if (pos.isWithinDistance(playerPos, config.iconRenderDistance()))
            renderIcons(icons, light, matrices, vertexConsumers, overlay);
        if (pos.isWithinDistance(playerPos, config.itemRenderDistance()))
            renderItem(item, light, matrices, vertexConsumers, seed);
    }
    
    protected final boolean shouldRender(T drawer, Direction facing) {
        var world = drawer.getWorld();
        if (world == null) return false;
        var pos = drawer.getPos();
        var state = drawer.getCachedState();
        
        return Block.shouldDrawSide(state, world, pos, facing, pos.offset(facing));
    }
    
    protected void renderIcons(List<Sprite> icons, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay) {
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

    protected void renderItem(ItemVariant item, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed) {
        if (item.isBlank()) return;
        var itemScale = ClientConfig.HANDLE.get().itemScale();

        matrices.push();
        matrices.scale(itemScale, itemScale, 1);
        matrices.scale(0.75f, 0.75f, 1);
        matrices.multiplyPositionMatrix(Matrix4f.scale(1, 1, 0.01f));

        var stack = item.toStack();
        var model = itemRenderer.getModel(stack, null, null, seed);

        // Stolen from storage drawers
        if (model.isSideLit()) {
            matrices.peek().getNormalMatrix().multiply(ITEM_LIGHT_ROTATION_3D);
        } else {
            matrices.peek().getNormalMatrix().multiply(ITEM_LIGHT_ROTATION_FLAT);
        }

        itemRenderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
        matrices.pop();
    }
    
    protected void renderText(long amount, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var itemScale = ClientConfig.HANDLE.get().itemScale();

        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.translate(0, 0.3, -0.01);
        matrices.scale(itemScale, itemScale, 1);
        matrices.scale(0.02f, 0.02f, 0.02f);
        var text = Long.toString(amount);
        textRenderer.draw(text, -textRenderer.getWidth(text) / 2f, 0, 0xffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0x000000, light);
        matrices.pop();
    }

    protected void alignMatrices(MatrixStack matrices, Direction dir) {
        var pos = dir.getUnitVector();
        matrices.translate(pos.getX() / 2 + 0.5, pos.getY() / 2 + 0.5, pos.getZ() / 2 + 0.5);
        matrices.multiply(dir.getRotationQuaternion());
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
        matrices.translate(0, 0, 0.01);
    }
}
