package io.github.mattidragon.extendeddrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public abstract class AbstractDrawerBlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState>
        implements BlockEntityRenderer<T, S> {
    private static final Quaternionf ITEM_LIGHT_ROTATION_3D = Axis.XP.rotationDegrees(-15).mul(Axis.YP.rotationDegrees(15));
    private static final Quaternionf ITEM_LIGHT_ROTATION_FLAT = Axis.XP.rotationDegrees(-45);

    private final Font font;
    private final SpriteGetter spriteGetter;
    protected final ItemModelResolver itemModelResolver;

    public AbstractDrawerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
        this.spriteGetter = context.sprites();
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void extractRenderState(T blockEntity, S state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        var dir = StorageDrawerBlock.getFront(blockEntity.getBlockState());
        state.lightCoords = LevelRenderer.getLightCoords(Objects.requireNonNull(blockEntity.getLevel()), blockEntity.getBlockPos().relative(dir));
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

    public void renderSlot(ItemStackRenderState item, @Nullable String amount, boolean small, boolean hidden, boolean full, Collection<SpriteId> icons, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState, int light, BlockPos pos) {
        var playerPos = cameraState.pos;
        var config = ExtendedDrawers.CONFIG.get().client();

        if (hidden) {
            renderHiddenOverlay(small, light, matrices, queue);
            return;
        }

        if (pos.closerToCenterThan(playerPos, config.textRenderDistance()) && amount != null) {
            renderText(amount, full && config.indicateFullDrawers(), small, light, matrices, queue);
        }
        if (pos.closerToCenterThan(playerPos, config.iconRenderDistance())) {
            renderIcons(icons, small, light, matrices, queue);
        }
        if (pos.closerToCenterThan(playerPos, config.itemRenderDistance())) {
            renderItem(item, small, light, matrices, queue);
        }
    }

    protected void renderHiddenOverlay(boolean small, int light, PoseStack matrices, SubmitNodeCollector queue) {
        matrices.pushPose();
        if (small) matrices.scale(0.5f, 0.5f, 1);
        matrices.mulPose(Axis.XP.rotationDegrees(90));
        matrices.translate(-0.5, -1, -0.5);

        @SuppressWarnings("deprecation")
        var spriteId = new SpriteId(TextureAtlas.LOCATION_BLOCKS, ExtendedDrawers.id("block/drawer_hidden_overlay"));
        var sprite = spriteGetter.get(spriteId);

        queue.submitCustomGeometry(matrices, RenderTypes.cutoutMovingBlock(), (matricesEntry, vertexConsumer) ->
                renderIcon(sprite, light, matricesEntry, vertexConsumer));

        matrices.popPose();
    }

    public void renderIcons(Collection<SpriteId> icons, boolean small, int light, PoseStack matrices, SubmitNodeCollector queue) {
        var increment = 1.0 / (icons.size() + 1.0);
        matrices.pushPose();
        if (small) matrices.scale(0.5f, 0.5f, 1);
        matrices.translate(-0.5, 0, 0);

        for (var icon : icons) {
            matrices.translate(increment, 0, 0.001);
            matrices.pushPose();
            matrices.mulPose(Axis.XP.rotationDegrees(90));
            matrices.translate(-0.125, -0.24, -0.5);
            matrices.scale(0.25f, 0.25f, 0.25f);

            var sprite = spriteGetter.get(icon);
            queue.submitCustomGeometry(matrices, RenderTypes.entityCutout(sprite.atlasLocation()), (matricesEntry, vertexConsumer) ->
                    renderIcon(sprite, light, matricesEntry, vertexConsumer));

            matrices.popPose();
        }
        matrices.popPose();
    }

    private void renderIcon(TextureAtlasSprite sprite, int light, PoseStack.Pose matrixEntry, VertexConsumer consumer) {
        var overlay = OverlayTexture.NO_OVERLAY;

        var minU = sprite.getU0();
        var maxU = sprite.getU1();
        var minV = sprite.getV0();
        var maxV = sprite.getV1();

        consumer.addVertex(matrixEntry, 0, 1, 0).setUv(minU, minV).setColor(0xFFFFFFFF).setLight(light).setOverlay(overlay).setNormal(matrixEntry, 0, 1, 0);
        consumer.addVertex(matrixEntry, 0, 1, 1).setUv(minU, maxV).setColor(0xFFFFFFFF).setLight(light).setOverlay(overlay).setNormal(matrixEntry, 0, 1, 0);
        consumer.addVertex(matrixEntry, 1, 1, 1).setUv(maxU, maxV).setColor(0xFFFFFFFF).setLight(light).setOverlay(overlay).setNormal(matrixEntry, 0, 1, 0);
        consumer.addVertex(matrixEntry, 1, 1, 0).setUv(maxU, minV).setColor(0xFFFFFFFF).setLight(light).setOverlay(overlay).setNormal(matrixEntry, 0, 1, 0);
    }

    public void renderItem(ItemStackRenderState item, boolean small, int light, PoseStack matrices, SubmitNodeCollector queue) {
        if (item.isEmpty()) return;
        var itemScale = ExtendedDrawers.CONFIG.get().client().layout().itemScale(small);

        matrices.pushPose();
        matrices.scale(itemScale, itemScale, 1);
        matrices.scale(0.75f, 0.75f, 1);
        matrices.last().pose().mul(new Matrix4f().scale(1, 1, 0.001f));

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

        item.submit(matrices, queue, light, OverlayTexture.NO_OVERLAY, 0);

//        // Restore light configuration
//        RenderSystem.setShaderLights(lights);
        
        matrices.popPose();
    }

    public void renderText(String amount, boolean full, boolean small, int light, PoseStack matrices, SubmitNodeCollector queue) {
        var config = ExtendedDrawers.CONFIG.get().client();

        matrices.pushPose();
        matrices.mulPose(Axis.XP.rotationDegrees(180));
        if (small) {
            matrices.translate(0, 0.25, 0);
        } else {
            matrices.translate(0, 0.5, 0);
        }
        matrices.scale(config.layout().textScale(small), config.layout().textScale(small), 1);
        matrices.translate(0, config.layout().textOffset(small) / -4, -0.001);

        matrices.scale(0.02f, 0.02f, 0.02f);
        queue.submitText(
                matrices,
                -font.width(amount) / 2f,
                0,
                Component.literal(amount).getVisualOrderText(),
                false,
                Font.DisplayMode.NORMAL,
                light,
                full ? 0xffffff00 : 0xffffffff,
                0x00000000,
                0x00000000
        );
        matrices.popPose();
    }

    protected void alignMatrices(PoseStack matrices, Direction dir, AttachFace face) {
        var pos = switch (face) {
            case FLOOR -> Direction.UP.step();
            case CEILING -> Direction.DOWN.step();
            default -> dir.step();
        };
        matrices.translate(pos.x / 2 + 0.5, pos.y / 2 + 0.5, pos.z / 2 + 0.5);
        // We only transform the position matrix as the normals have to stay in the old configuration for item lighting
        matrices.last().pose().rotate(dir.getRotation());
        switch (face) {
            case FLOOR -> matrices.last().pose().rotate(Axis.XP.rotationDegrees(-90));
            case CEILING -> matrices.last().pose().rotate(Axis.XP.rotationDegrees(90));
        }
        matrices.last().pose().rotate(Axis.XP.rotationDegrees(-90));
        matrices.translate(0, 0, 0.01);
    }
}
