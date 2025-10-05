package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.client.renderer.state.CompactingDrawerRenderState;
import io.github.mattidragon.extendeddrawers.client.renderer.state.CompactingSlotRenderState;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CompactingDrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<CompactingDrawerBlockEntity, CompactingDrawerRenderState> {
    public CompactingDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public CompactingDrawerRenderState createRenderState() {
        return new CompactingDrawerRenderState();
    }

    @Override
    public void updateRenderState(CompactingDrawerBlockEntity drawer, CompactingDrawerRenderState state, float tickProgress, Vec3d cameraPos, ModelCommandRenderer.@Nullable CrumblingOverlayCommand crumblingOverlay) {
        super.updateRenderState(drawer, state, tickProgress, cameraPos, crumblingOverlay);
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
            var slot = block.getSlot(drawer, block.getSlotIndex(drawer, new Vec2f(0.5f, 0.25f)));
            slotState.disabled = slot.isBlocked();
            slotState.amount = slot.getAmount();
            itemModelManager.update(slotState.item, slot.getResource().toStack(), ItemDisplayContext.GUI, drawer.getWorld(), null, drawer.getPos().hashCode());
        } else {
            slotState.disabled = true;
            slotState.item.clear();
        }

        slotState = state.slots[1];
        if (activeSlots.length >= 2) {
            var slot = block.getSlot(drawer, block.getSlotIndex(drawer, new Vec2f(0.75f, 0.75f)));
            slotState.disabled = slot.isBlocked();
            slotState.amount = slot.getAmount();
            itemModelManager.update(slotState.item, slot.getResource().toStack(), ItemDisplayContext.GUI, drawer.getWorld(), null, drawer.getPos().hashCode());
        } else {
            slotState.disabled = true;
            slotState.item.clear();
        }

        slotState = state.slots[2];
        if (activeSlots.length >= 3) {
            var slot = block.getSlot(drawer, block.getSlotIndex(drawer, new Vec2f(0.25f, 0.75f)));
            slotState.disabled = slot.isBlocked();
            slotState.amount = slot.getAmount();
            itemModelManager.update(slotState.item, slot.getResource().toStack(), ItemDisplayContext.GUI, drawer.getWorld(), null, drawer.getPos().hashCode());
        } else {
            slotState.disabled = true;
            slotState.item.clear();
        }
    }

    @Override
    public void render(CompactingDrawerRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        var horizontalDir = state.blockState.get(StorageDrawerBlock.FACING);
        var face = state.blockState.get(StorageDrawerBlock.FACE);

        matrices.push();
        alignMatrices(matrices, horizontalDir, face);
        var light = state.lightmapCoordinates;

        if (state.isHidden) {
            renderHiddenOverlay(false, light, matrices, queue);
            matrices.pop();
            return;
        }

        renderIcons(state, matrices, light, queue, cameraState);

        matrices.translate(0, 0.25, 0);
        renderSlot(state, state.slots[0], matrices, light, queue, cameraState);
        matrices.translate(0.25, -0.5, 0);
        renderSlot(state, state.slots[1], matrices, light, queue, cameraState);
        matrices.translate(-0.5, 0, 0);
        renderSlot(state, state.slots[2], matrices, light, queue, cameraState);

        matrices.pop();
    }

    @Override
    public int getRenderDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }

    private void renderIcons(CompactingDrawerRenderState state, MatrixStack matrices, int light, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        var icons = new ArrayList<SpriteIdentifier>();
        var config = ExtendedDrawers.CONFIG.get().client().icons();
        @SuppressWarnings("deprecation")
        var blockAtlas = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;

        if (state.isLocked) icons.add(new SpriteIdentifier(blockAtlas, config.lockedIcon()));
        if (state.isVoiding) icons.add(new SpriteIdentifier(blockAtlas, config.voidingIcon()));
        if (state.isHidden) icons.add(new SpriteIdentifier(blockAtlas, config.hiddenIcon()));
        if (state.isDuping) icons.add(new SpriteIdentifier(blockAtlas, config.dupingIcon()));
        if (state.upgrade != null) icons.add(new SpriteIdentifier(blockAtlas, state.upgrade.sprite));
        if (state.hasLimiter) icons.add(new SpriteIdentifier(blockAtlas, ExtendedDrawers.id("item/limiter")));

        var playerPos = cameraState.entityPos;
        if (state.pos.isWithinDistance(playerPos, ExtendedDrawers.CONFIG.get().client().iconRenderDistance())) {
            matrices.push(); // Render icons like the top slot
            matrices.translate(0, 0.25, 0);
            renderIcons(icons, true, light, matrices, queue);
            matrices.pop();
        }
    }

    private void renderSlot(CompactingDrawerRenderState state, CompactingSlotRenderState slot, MatrixStack matrices, int light, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        if (slot.disabled) return;

        @Nullable
        String amount = String.valueOf(slot.amount);
        if ((slot.amount == 0) && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;
        if (state.isDuping)
            amount = "∞";

        var item = slot.item;
        renderSlot(item, amount, true, false, List.of(), matrices, queue, cameraState, light, state.pos);
    }
}
