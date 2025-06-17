package io.github.mattidragon.extendeddrawers.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public abstract class AbstractDrawerBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    private static final Quaternionf ITEM_LIGHT_ROTATION_3D = RotationAxis.POSITIVE_X.rotationDegrees(-15).mul(RotationAxis.POSITIVE_Y.rotationDegrees(15));
    private static final Quaternionf ITEM_LIGHT_ROTATION_FLAT = RotationAxis.POSITIVE_X.rotationDegrees(-45);

    private final ItemModelManager itemModelManager;
    private final TextRenderer textRenderer;
    private final ItemRenderState itemRenderState = new ItemRenderState();

    public AbstractDrawerBlockEntityRenderer(ItemModelManager itemModelManager, TextRenderer textRenderer) {
        this.itemModelManager = itemModelManager;
        this.textRenderer = textRenderer;
    }

    /**
     * Creates an instance for rendering slots in guis for layout preview.
     */
    public static AbstractDrawerBlockEntityRenderer<BlockEntity> createRendererTool() {
        var client = MinecraftClient.getInstance();
        return new AbstractDrawerBlockEntityRenderer<>(client.getItemModelManager(), client.textRenderer) {
            @Override
            public void render(BlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {

            }
        };
    }

    public void renderSlot(ItemVariant item, @Nullable String amount, boolean small, boolean hidden, List<Sprite> icons, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int seed, BlockPos pos, World world) {
        var player = MinecraftClient.getInstance().player;
        var playerPos = player == null ? Vec3d.ofCenter(pos) : player.getPos();
        var config = ExtendedDrawers.CONFIG.get().client();

        if (hidden) {
            renderHiddenOverlay(small, light, overlay, matrices, vertexConsumers);
            return;
        }
    
        if (pos.isWithinDistance(playerPos, config.textRenderDistance()) && amount != null)
            renderText(amount, small, light, matrices, vertexConsumers);
        if (pos.isWithinDistance(playerPos, config.iconRenderDistance()))
            renderIcons(icons, small, light, overlay, matrices, vertexConsumers);
        if (pos.isWithinDistance(playerPos, config.itemRenderDistance()))
            renderItem(item, small, light, matrices, vertexConsumers, world, seed);
    }

    protected void renderHiddenOverlay(boolean small, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        if (small) matrices.scale(0.5f, 0.5f, 1);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.translate(-0.5, 0, -0.5);

        @SuppressWarnings("deprecation")
        var sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
                .apply(ExtendedDrawers.id("block/drawer_hidden_overlay"));

        var consumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

        var minU = sprite.getMinU();
        var maxU = sprite.getMaxU();
        var minV = sprite.getMinV();
        var maxV = sprite.getMaxV();

        consumer.vertex(matrices.peek(), 0, 0, 0).texture(minU, minV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(matrices.peek(), 0, 0, 1).texture(minU, maxV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(matrices.peek(), 1, 0, 1).texture(maxU, maxV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(matrices.peek(), 1, 0, 0).texture(maxU, minV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrices.peek(), 0, 1, 0);

        matrices.pop();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean shouldRender(T drawer, Direction facing) {
        var world = drawer.getWorld();
        if (world == null) return false;
        var pos = drawer.getPos();
        var state = drawer.getCachedState();
        
        return Block.shouldDrawSide(state, world.getBlockState(pos.offset(facing)), facing);
    }

    public void renderIcons(List<Sprite> icons, boolean small, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var increment = 1.0 / (icons.size() + 1.0);
        matrices.push();
        if (small) matrices.scale(0.5f, 0.5f, 1);
        matrices.translate(-0.5, 0, 0);

        for (var icon : icons) {
            matrices.translate(increment, 0, 0.001);
            renderIcon(icon, light, overlay, matrices, vertexConsumers);
        }
        matrices.pop();
    }

    private void renderIcon(Sprite sprite, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.translate(-0.125, -0.24, -0.5);
        matrices.scale(0.25f, 0.25f, 0.25f);
        var consumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

        var minU = sprite.getMinU();
        var maxU = sprite.getMaxU();
        var minV = sprite.getMinV();
        var maxV = sprite.getMaxV();

        consumer.vertex(matrices.peek(), 0, 1, 0).texture(minU, minV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(matrices.peek(), 0, 1, 1).texture(minU, maxV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(matrices.peek(), 1, 1, 1).texture(maxU, maxV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrices.peek(), 0, 1, 0);
        consumer.vertex(matrices.peek(), 1, 1, 0).texture(maxU, minV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrices.peek(), 0, 1, 0);

        matrices.pop();
    }

    public void renderItem(ItemVariant item, boolean small, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, World world, int seed) {
        if (item.isBlank()) return;
        var itemScale = ExtendedDrawers.CONFIG.get().client().layout().itemScale(small);

        matrices.push();
        matrices.scale(itemScale, itemScale, 1);
        matrices.scale(0.75f, 0.75f, 1);
        matrices.peek().getPositionMatrix().mul(new Matrix4f().scale(1, 1, 0.01f));
        
        var stack = item.toStack();
        itemModelManager.clearAndUpdate(itemRenderState, stack, ItemDisplayContext.GUI, world, null, seed);

        // Copy existing light configuration
        var lights = RenderSystem.getShaderLights();

        var diffuseLighting = MinecraftClient.getInstance().gameRenderer.getDiffuseLighting();

        // Set up gui lighting
        if (itemRenderState.isSideLit()) {
            matrices.peek().getNormalMatrix().rotate(ITEM_LIGHT_ROTATION_3D);
            diffuseLighting.setShaderLights(DiffuseLighting.Type.ITEMS_3D);
        } else {
            matrices.peek().getNormalMatrix().rotate(ITEM_LIGHT_ROTATION_FLAT);
            diffuseLighting.setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
        }

        itemRenderState.render(matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

        // Restore light configuration
        RenderSystem.setShaderLights(lights);
        
        matrices.pop();
    }

    public void renderText(String amount, boolean small, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        var config = ExtendedDrawers.CONFIG.get().client();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        if (small) {
            matrices.translate(0, 0.25, -0.01);
        } else {
            matrices.translate(0, 0.5, -0.01);
        }
        matrices.scale(config.layout().textScale(small), config.layout().textScale(small), 1);
        matrices.translate(0, config.layout().textOffset() / -4, -0.01);

        matrices.scale(0.02f, 0.02f, 0.02f);
        textRenderer.draw(amount, -textRenderer.getWidth(amount) / 2f, 0, 0xffffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0x00000000, light);
        matrices.pop();
    }

    protected void alignMatrices(MatrixStack matrices, Direction dir, BlockFace face) {
        var pos = switch (face) {
            case FLOOR -> Direction.UP.getUnitVector();
            case CEILING -> Direction.DOWN.getUnitVector();
            default -> dir.getUnitVector();
        };
        matrices.translate(pos.x / 2 + 0.5, pos.y / 2 + 0.5, pos.z / 2 + 0.5);
        // We only transform the position matrix as the normals have to stay in the old configuration for item lighting
        matrices.peek().getPositionMatrix().rotate(dir.getRotationQuaternion());
        switch (face) {
            case FLOOR -> matrices.peek().getPositionMatrix().rotate(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            case CEILING -> matrices.peek().getPositionMatrix().rotate(RotationAxis.POSITIVE_X.rotationDegrees(90));
        }
        matrices.peek().getPositionMatrix().rotate(RotationAxis.POSITIVE_X.rotationDegrees(-90));
        matrices.translate(0, 0, 0.01);
    }
}
