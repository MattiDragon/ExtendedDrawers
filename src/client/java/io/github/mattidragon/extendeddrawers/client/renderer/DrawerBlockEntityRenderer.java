package io.github.mattidragon.extendeddrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.client.renderer.state.DrawerRenderState;
import io.github.mattidragon.extendeddrawers.client.renderer.state.DrawerSlotRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<DrawerBlockEntity, DrawerRenderState> {
    public DrawerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public DrawerRenderState createRenderState() {
        return new DrawerRenderState();
    }

    @Override
    public void extractRenderState(DrawerBlockEntity drawer, DrawerRenderState state, float tickProgress, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(drawer, state, tickProgress, cameraPos, crumblingOverlay);

        state.slotCount = drawer.slots;
        @Nullable DrawerSlotRenderState[] slots = state.slots;
        if (state.slots.length != drawer.slots) {
            slots = state.slots = new DrawerSlotRenderState[drawer.slots];
        }

        for (var i = 0; i < drawer.storages.length; i++) {
            var slotState = slots[i];
            if (slotState == null) slotState = new DrawerSlotRenderState();
            var slot = drawer.storages[i];

            slotState.isLocked = slot.isLocked();
            slotState.isVoiding = slot.isVoiding();
            slotState.isHidden = slot.isHidden();
            slotState.isDuping = slot.isDuping();
            slotState.upgrade = slot.getUpgrade();
            slotState.hasLimiter = slot.hasLimiter();
            itemModelResolver.appendItemLayers(slotState.item, slot.getResource().toStack(), ItemDisplayContext.GUI, drawer.getLevel(), null, drawer.getBlockPos().hashCode() * i);
            slotState.amount = slot.getAmount();

            state.slots[i] = slotState;
        }

        state.blockState = drawer.getBlockState();
    }

    @Override
    public void submit(DrawerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        var horizontalDir = state.blockState.getValue(StorageDrawerBlock.FACING);
        var face = state.blockState.getValue(StorageDrawerBlock.FACE);

        // TODO: shouldRender()

        poseStack.pushPose();
        alignMatrices(poseStack, horizontalDir, face);
        var light = state.lightCoords;
        var slots = state.slotCount;
        var pos = state.blockPos;

        switch (slots) {
            case 1 -> renderSlot(state.slots[0], false, light, poseStack, submitNodeCollector, camera, pos);
            case 2 -> {
                poseStack.translate(-0.25, 0, 0);
                renderSlot(state.slots[0], true, light, poseStack, submitNodeCollector, camera, pos);
                poseStack.translate(0.5, 0, 0);
                renderSlot(state.slots[1], true, light, poseStack, submitNodeCollector, camera, pos);
            }
            case 4 -> {
                poseStack.translate(-0.25, 0.25, 0);
                renderSlot(state.slots[0], true, light, poseStack, submitNodeCollector, camera, pos);
                poseStack.translate(0.5, 0, 0);
                renderSlot(state.slots[1], true, light, poseStack, submitNodeCollector, camera, pos);
                poseStack.translate(-0.5, -0.5, 0);
                renderSlot(state.slots[2], true, light, poseStack, submitNodeCollector, camera, pos);
                poseStack.translate(0.5, 0, 0);
                renderSlot(state.slots[3], true, light, poseStack, submitNodeCollector, camera, pos);
            }
            default -> ExtendedDrawers.LOGGER.error("Unexpected drawer slot count, skipping rendering. Are you an addon dev adding more configurations? If so please mixin into DrawerBlockEntityRenderer and add your layout.");
        }

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }
    
    private void renderSlot(DrawerSlotRenderState slotState, boolean small, int light, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState, BlockPos pos) {
        var icons = getIconsForSlot(slotState);

        String amount = String.valueOf(slotState.amount);
        if ((slotState.amount == 0) && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;
        if (slotState.isDuping)
            amount = "∞";

        renderSlot(slotState.item, amount, small, slotState.isHidden, icons, matrices, queue, cameraState, light, pos);
    }

    private static List<SpriteId> getIconsForSlot(DrawerSlotRenderState slotState) {
        var icons = new ArrayList<SpriteId>();
        var config = ExtendedDrawers.CONFIG.get().client().icons();
        @SuppressWarnings("deprecation")
        var atlas = TextureAtlas.LOCATION_ITEMS;

        if (slotState.isLocked) icons.add(new SpriteId(atlas, config.lockedIcon()));
        if (slotState.isVoiding) icons.add(new SpriteId(atlas, config.voidingIcon()));
        if (slotState.isHidden) icons.add(new SpriteId(atlas, config.hiddenIcon()));
        if (slotState.isDuping) icons.add(new SpriteId(atlas, config.dupingIcon()));
        if (slotState.upgrade != null) icons.add(new SpriteId(atlas, slotState.upgrade.sprite));
        if (slotState.hasLimiter) icons.add(new SpriteId(atlas, ExtendedDrawers.id("item/limiter")));
        return icons;
    }
}
