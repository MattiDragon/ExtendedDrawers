package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.client.renderer.state.DrawerRenderState;
import io.github.mattidragon.extendeddrawers.client.renderer.state.DrawerSlotRenderState;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<DrawerBlockEntity, DrawerRenderState> {
    public DrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public DrawerRenderState createRenderState() {
        return new DrawerRenderState();
    }

    @Override
    public void updateRenderState(DrawerBlockEntity drawer, DrawerRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        super.updateRenderState(drawer, state, tickProgress, cameraPos, crumblingOverlay);
        state.slotCount = drawer.slots;
        if (state.slots == null || state.slots.length != drawer.slots) {
            state.slots = new DrawerSlotRenderState[drawer.slots];
        }
        for (var i = 0; i < drawer.storages.length; i++) {
            var slotState = state.slots[i];
            if (slotState == null) slotState = new DrawerSlotRenderState();
            var slot = drawer.storages[i];

            slotState.isLocked = slot.isLocked();
            slotState.isVoiding = slot.isVoiding();
            slotState.isHidden = slot.isHidden();
            slotState.isDuping = slot.isDuping();
            slotState.upgrade = slot.getUpgrade();
            slotState.hasLimiter = slot.hasLimiter();
            itemModelManager.update(slotState.item, slot.getResource().toStack(), ItemDisplayContext.GUI, drawer.getWorld(), null, drawer.getPos().hashCode() * i);
            slotState.amount = slot.getAmount();

            state.slots[i] = slotState;
        }
    }

    @Override
    public void render(DrawerRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        var horizontalDir = state.blockState.get(StorageDrawerBlock.FACING);
        var face = state.blockState.get(StorageDrawerBlock.FACE);

        // TODO: shouldRender()

        matrices.push();
        alignMatrices(matrices, horizontalDir, face);
        var light = state.lightmapCoordinates;
        var slots = state.slotCount;
        var pos = state.pos;

        switch (slots) {
            case 1 -> renderSlot(state.slots[0], false, light, matrices, queue, cameraState, pos);
            case 2 -> {
                matrices.translate(-0.25, 0, 0);
                renderSlot(state.slots[0], true, light, matrices, queue, cameraState, pos);
                matrices.translate(0.5, 0, 0);
                renderSlot(state.slots[1], true, light, matrices, queue, cameraState, pos);
            }
            case 4 -> {
                matrices.translate(-0.25, 0.25, 0);
                renderSlot(state.slots[0], true, light, matrices, queue, cameraState, pos);
                matrices.translate(0.5, 0, 0);
                renderSlot(state.slots[1], true, light, matrices, queue, cameraState, pos);
                matrices.translate(-0.5, -0.5, 0);
                renderSlot(state.slots[2], true, light, matrices, queue, cameraState, pos);
                matrices.translate(0.5, 0, 0);
                renderSlot(state.slots[3], true, light, matrices, queue, cameraState, pos);
            }
            default -> ExtendedDrawers.LOGGER.error("Unexpected drawer slot count, skipping rendering. Are you an addon dev adding more configurations? If so please mixin into DrawerBlockEntityRenderer and add your layout.");
        }

        matrices.pop();
    }

    @Override
    public int getRenderDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }
    
    private void renderSlot(DrawerSlotRenderState slotState, boolean small, int light, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, BlockPos pos) {
        var icons = new ArrayList<SpriteIdentifier>();
        var config = ExtendedDrawers.CONFIG.get().client().icons();
        @SuppressWarnings("deprecation")
        var blockAtlas = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
        
        if (slotState.isLocked) icons.add(new SpriteIdentifier(blockAtlas, config.lockedIcon()));
        if (slotState.isVoiding) icons.add(new SpriteIdentifier(blockAtlas, config.voidingIcon()));
        if (slotState.isHidden) icons.add(new SpriteIdentifier(blockAtlas, config.hiddenIcon()));
        if (slotState.isDuping) icons.add(new SpriteIdentifier(blockAtlas, config.dupingIcon()));
        if (slotState.upgrade != null) icons.add(new SpriteIdentifier(blockAtlas, slotState.upgrade.sprite));
        if (slotState.hasLimiter) icons.add(new SpriteIdentifier(blockAtlas, ExtendedDrawers.id("item/limiter")));

        String amount = String.valueOf(slotState.amount);
        if ((slotState.amount == 0) && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;
        if (slotState.isDuping)
            amount = "∞";

        renderSlot(slotState.item, amount, small, slotState.isHidden, icons, matrices, queue, cameraState, light, pos);
    }
}
