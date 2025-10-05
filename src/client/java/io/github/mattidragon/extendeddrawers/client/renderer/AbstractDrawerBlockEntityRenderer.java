package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.Collection;
import java.util.Objects;

public abstract class AbstractDrawerBlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState>
        implements BlockEntityRenderer<T, S> {
    private static final Quaternionf ITEM_LIGHT_ROTATION_3D = RotationAxis.POSITIVE_X.rotationDegrees(-15).mul(RotationAxis.POSITIVE_Y.rotationDegrees(15));
    private static final Quaternionf ITEM_LIGHT_ROTATION_FLAT = RotationAxis.POSITIVE_X.rotationDegrees(-45);

    private final TextRenderer textRenderer;
    private final SpriteHolder spriteHolder;
    protected final ItemModelManager itemModelManager;

    public AbstractDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.textRenderer = context.textRenderer();
        this.spriteHolder = context.spriteHolder();
        this.itemModelManager = context.itemModelManager();
    }

    @Override
    public void updateRenderState(T blockEntity, S state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
        var dir = StorageDrawerBlock.getFront(blockEntity.getCachedState());
        state.lightmapCoordinates = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(blockEntity.getWorld()), blockEntity.getPos().offset(dir));
    }

    // TODO: update
//    /**
//     * Creates an instance for rendering slots in guis for layout preview.
//     */
//    public static AbstractDrawerBlockEntityRenderer<BlockEntity> createRendererTool() {
//        var client = MinecraftClient.getInstance();
//        return new AbstractDrawerBlockEntityRenderer<>(client.textRenderer, context.spriteHolder()) {
//            @Override
//            public void render(BlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
//
//            }
//        };
//    }

    public void renderSlot(ItemRenderState item, String amount, boolean small, boolean hidden, Collection<SpriteIdentifier> icons, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, int light, BlockPos pos) {
        var playerPos = cameraState.pos;
        var config = ExtendedDrawers.CONFIG.get().client();

        if (hidden) {
            renderHiddenOverlay(small, light, matrices, queue);
            return;
        }

        if (pos.isWithinDistance(playerPos, config.textRenderDistance()) && amount != null) {
            renderText(amount, small, light, matrices, queue);
        }
        if (pos.isWithinDistance(playerPos, config.iconRenderDistance())) {
            renderIcons(icons, small, light, matrices, queue);
        }
        if (pos.isWithinDistance(playerPos, config.itemRenderDistance())) {
            renderItem(item, small, light, matrices, queue);
        }
    }

    protected void renderHiddenOverlay(boolean small, int light, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        matrices.push();
        if (small) matrices.scale(0.5f, 0.5f, 1);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.translate(-0.5, -1, -0.5);

        @SuppressWarnings("deprecation")
        var spriteId = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, ExtendedDrawers.id("block/drawer_hidden_overlay"));
        var sprite = spriteHolder.getSprite(spriteId);

        queue.submitCustom(matrices, RenderLayer.getCutout(), (matricesEntry, vertexConsumer) ->
                renderIcon(sprite, light, matricesEntry, vertexConsumer));

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

    public void renderIcons(Collection<SpriteIdentifier> icons, boolean small, int light, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        var increment = 1.0 / (icons.size() + 1.0);
        matrices.push();
        if (small) matrices.scale(0.5f, 0.5f, 1);
        matrices.translate(-0.5, 0, 0);

        for (var icon : icons) {
            matrices.translate(increment, 0, 0.001);
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            matrices.translate(-0.125, -0.24, -0.5);
            matrices.scale(0.25f, 0.25f, 0.25f);

            var sprite = spriteHolder.getSprite(icon);
            queue.submitCustom(matrices, RenderLayer.getCutout(), (matricesEntry, vertexConsumer) ->
                    renderIcon(sprite, light, matricesEntry, vertexConsumer));

            matrices.pop();
        }
        matrices.pop();
    }

    private void renderIcon(Sprite sprite, int light, MatrixStack.Entry matrixEntry, VertexConsumer consumer) {
        var overlay = OverlayTexture.DEFAULT_UV;

        var minU = sprite.getMinU();
        var maxU = sprite.getMaxU();
        var minV = sprite.getMinV();
        var maxV = sprite.getMaxV();

        consumer.vertex(matrixEntry, 0, 1, 0).texture(minU, minV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrixEntry, 0, 1, 0);
        consumer.vertex(matrixEntry, 0, 1, 1).texture(minU, maxV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrixEntry, 0, 1, 0);
        consumer.vertex(matrixEntry, 1, 1, 1).texture(maxU, maxV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrixEntry, 0, 1, 0);
        consumer.vertex(matrixEntry, 1, 1, 0).texture(maxU, minV).color(0xFFFFFFFF).light(light).overlay(overlay).normal(matrixEntry, 0, 1, 0);
    }

    public void renderItem(ItemRenderState item, boolean small, int light, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        if (item.isEmpty()) return;
        var itemScale = ExtendedDrawers.CONFIG.get().client().layout().itemScale(small);

        matrices.push();
        matrices.scale(itemScale, itemScale, 1);
        matrices.scale(0.75f, 0.75f, 1);
        matrices.peek().getPositionMatrix().mul(new Matrix4f().scale(1, 1, 0.01f));

        // TODO: Fix lighting hack
//        // Copy existing light configuration
//        var lights = RenderSystem.getShaderLights();
//
//        var diffuseLighting = MinecraftClient.getInstance().gameRenderer.getDiffuseLighting();

//        // Set up gui lighting
//        if (item.isSideLit()) {
//            matrices.peek().getNormalMatrix().rotate(ITEM_LIGHT_ROTATION_3D);
//            diffuseLighting.setShaderLights(DiffuseLighting.Type.ITEMS_3D);
//        } else {
//            matrices.peek().getNormalMatrix().rotate(ITEM_LIGHT_ROTATION_FLAT);
//            diffuseLighting.setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);
//        }

        item.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, 0);

//        // Restore light configuration
//        RenderSystem.setShaderLights(lights);
        
        matrices.pop();
    }

    public void renderText(String amount, boolean small, int light, MatrixStack matrices, OrderedRenderCommandQueue queue) {
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
        queue.submitText(
                matrices,
                -textRenderer.getWidth(amount) / 2f,
                0,
                Text.literal(amount).asOrderedText(),
                false,
                TextRenderer.TextLayerType.NORMAL,
                light,
                0xffffffff,
                0x00000000,
                0x00000000
        );
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
