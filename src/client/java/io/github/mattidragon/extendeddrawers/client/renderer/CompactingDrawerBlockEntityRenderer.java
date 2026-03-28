package io.github.mattidragon.extendeddrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.client.renderer.state.CompactingDrawerRenderState;
import io.github.mattidragon.extendeddrawers.client.renderer.state.CompactingSlotRenderState;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CompactingDrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<CompactingDrawerBlockEntity, CompactingDrawerRenderState> {
    public CompactingDrawerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public CompactingDrawerRenderState createRenderState() {
        return new CompactingDrawerRenderState();
    }

    @Override
    public void extractRenderState(CompactingDrawerBlockEntity drawer, CompactingDrawerRenderState state, float tickProgress, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(drawer, state, tickProgress, cameraPos, crumblingOverlay);
        state.blockState = drawer.getBlockState();

        state.isLocked = drawer.storage.isLocked();
        state.isVoiding = drawer.storage.isVoiding();
        state.isHidden = drawer.storage.isHidden();
        state.isDuping = drawer.storage.isDuping();
        state.upgrade = drawer.storage.getUpgrade();
        state.hasLimiter = drawer.storage.hasLimiter();

        var block = ModBlocks.COMPACTING_DRAWER;

        var activeSlots = drawer.storage.getActiveSlotArray();

        var slotState = state.slots[0];
        if (activeSlots.length >= 1) {
            var slot = block.getSlot(drawer, block.getSlotIndex(drawer, new Vec2(0.5f, 0.25f)));
            slotState.disabled = slot.isBlocked();
            slotState.amount = slot.getAmount();
            itemModelResolver.appendItemLayers(slotState.item, slot.getResource().toStack(), ItemDisplayContext.GUI, drawer.getLevel(), null, drawer.getBlockPos().hashCode());
        } else {
            slotState.disabled = true;
            slotState.item.clear();
        }

        slotState = state.slots[1];
        if (activeSlots.length >= 2) {
            var slot = block.getSlot(drawer, block.getSlotIndex(drawer, new Vec2(0.75f, 0.75f)));
            slotState.disabled = slot.isBlocked();
            slotState.amount = slot.getAmount();
            itemModelResolver.appendItemLayers(slotState.item, slot.getResource().toStack(), ItemDisplayContext.GUI, drawer.getLevel(), null, drawer.getBlockPos().hashCode());
        } else {
            slotState.disabled = true;
            slotState.item.clear();
        }

        slotState = state.slots[2];
        if (activeSlots.length >= 3) {
            var slot = block.getSlot(drawer, block.getSlotIndex(drawer, new Vec2(0.25f, 0.75f)));
            slotState.disabled = slot.isBlocked();
            slotState.amount = slot.getAmount();
            itemModelResolver.appendItemLayers(slotState.item, slot.getResource().toStack(), ItemDisplayContext.GUI, drawer.getLevel(), null, drawer.getBlockPos().hashCode());
        } else {
            slotState.disabled = true;
            slotState.item.clear();
        }
    }

    @Override
    public void submit(CompactingDrawerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        var horizontalDir = state.blockState.getValue(StorageDrawerBlock.FACING);
        var face = state.blockState.getValue(StorageDrawerBlock.FACE);

        poseStack.pushPose();
        alignMatrices(poseStack, horizontalDir, face);
        var light = state.lightCoords;

        if (state.isHidden) {
            renderHiddenOverlay(false, light, poseStack, submitNodeCollector);
            poseStack.popPose();
            return;
        }

        renderIcons(state, poseStack, light, submitNodeCollector, camera);

        poseStack.translate(0, 0.25, 0);
        renderSlot(state, state.slots[0], poseStack, light, submitNodeCollector, camera);
        poseStack.translate(0.25, -0.5, 0);
        renderSlot(state, state.slots[1], poseStack, light, submitNodeCollector, camera);
        poseStack.translate(-0.5, 0, 0);
        renderSlot(state, state.slots[2], poseStack, light, submitNodeCollector, camera);

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }

    private void renderIcons(CompactingDrawerRenderState state, PoseStack matrices, int light, SubmitNodeCollector queue, CameraRenderState cameraState) {
        var icons = new ArrayList<SpriteId>();
        var config = ExtendedDrawers.CONFIG.get().client().icons();
        @SuppressWarnings("deprecation")
        var atlas = TextureAtlas.LOCATION_ITEMS;

        if (state.isLocked) icons.add(new SpriteId(atlas, config.lockedIcon()));
        if (state.isVoiding) icons.add(new SpriteId(atlas, config.voidingIcon()));
        if (state.isHidden) icons.add(new SpriteId(atlas, config.hiddenIcon()));
        if (state.isDuping) icons.add(new SpriteId(atlas, config.dupingIcon()));
        if (state.upgrade != null) icons.add(new SpriteId(atlas, state.upgrade.sprite));
        if (state.hasLimiter) icons.add(new SpriteId(atlas, ExtendedDrawers.id("item/limiter")));

        var playerPos = cameraState.pos;
        if (state.blockPos.closerToCenterThan(playerPos, ExtendedDrawers.CONFIG.get().client().iconRenderDistance())) {
            matrices.pushPose(); // Render icons like the top slot
            matrices.translate(0, 0.25, 0);
            renderIcons(icons, true, light, matrices, queue);
            matrices.popPose();
        }
    }

    private void renderSlot(CompactingDrawerRenderState state, CompactingSlotRenderState slot, PoseStack matrices, int light, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (slot.disabled) return;

        String amount = String.valueOf(slot.amount);
        if ((slot.amount == 0) && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;
        if (state.isDuping)
            amount = "∞";

        var item = slot.item;
        renderSlot(item, amount, true, false, List.of(), matrices, queue, cameraState, light, state.blockPos);
    }
}
