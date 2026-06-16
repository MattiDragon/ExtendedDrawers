package io.github.mattidragon.extendeddrawers.client.config.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.ModBlocks;
import io.github.mattidragon.extendeddrawers.client.renderer.state.CompactingDrawerRenderState;
import io.github.mattidragon.extendeddrawers.client.renderer.state.DrawerRenderState;
import io.github.mattidragon.extendeddrawers.client.renderer.state.DrawerSlotRenderState;
import io.github.mattidragon.extendeddrawers.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;

public class LayoutPreviewRenderer extends PictureInPictureRenderer<LayoutPreviewRenderState> {
    @Override
    public Class<LayoutPreviewRenderState> getRenderStateClass() {
        return LayoutPreviewRenderState.class;
    }

    @Override
    protected void renderToTexture(LayoutPreviewRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        if (!areComponentsBound()) {
            return;
        }

        var minecraft = Minecraft.getInstance();
        minecraft.gameRenderer.lighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

        poseStack.pushPose();
        poseStack.scale(state.size(), state.size(), state.size());
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.last().normal().rotate(Axis.XP.rotationDegrees(-90));

        var state1 = new DrawerRenderState();
        state1.blockEntityType = ModBlocks.DRAWER_BLOCK_ENTITY;
        state1.lightCoords = LightCoordsUtil.FULL_BRIGHT;
        state1.slotCount = 1;
        state1.slots = new DrawerSlotRenderState[] {
                Util.make(new DrawerSlotRenderState(), slot -> {
                    minecraft.getItemModelResolver()
                            .appendItemLayers(slot.item, Items.COBBLESTONE.getDefaultInstance(), ItemDisplayContext.GUI, null, null, 0);
                    slot.amount = 1024;
                    slot.isFull = true;
                    slot.isLocked = true;
                })
        };

        var state2 = new DrawerRenderState();
        state2.blockEntityType = ModBlocks.DRAWER_BLOCK_ENTITY;
        state2.slotCount = 4;
        state2.slots = new DrawerSlotRenderState[] {
                Util.make(new DrawerSlotRenderState(), slot -> {
                    minecraft.getItemModelResolver()
                            .appendItemLayers(slot.item, Items.REDSTONE.getDefaultInstance(), ItemDisplayContext.GUI, null, null, 0);
                    slot.amount = 16;
                    slot.isLocked = true;
                }),
                Util.make(new DrawerSlotRenderState(), slot -> {
                    minecraft.getItemModelResolver()
                            .appendItemLayers(slot.item, Items.GUNPOWDER.getDefaultInstance(), ItemDisplayContext.GUI, null, null, 0);
                    slot.amount = 32;
                    slot.isVoiding = true;
                }),
                Util.make(new DrawerSlotRenderState(), slot -> {
                    minecraft.getItemModelResolver()
                            .appendItemLayers(slot.item, Items.SUGAR.getDefaultInstance(), ItemDisplayContext.GUI, null, null, 0);
                    slot.amount = 64;
                    slot.isLocked = true;
                    slot.isVoiding = true;
                    slot.upgrade = ModItems.T2_UPGRADE;
                }),
                Util.make(new DrawerSlotRenderState(), slot -> {
                    minecraft.getItemModelResolver()
                            .appendItemLayers(slot.item, Items.GLOWSTONE.getDefaultInstance(), ItemDisplayContext.GUI, null, null, 0);
                    slot.amount = 128;
                    slot.upgrade = ModItems.T4_UPGRADE;
                })
        };

        var state3 = new CompactingDrawerRenderState();
        state3.blockEntityType = ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY;
        minecraft.getItemModelResolver()
                .appendItemLayers(state3.slots[2].item, Items.IRON_NUGGET.getDefaultInstance(), ItemDisplayContext.GUI, null, null, 0);
        state3.slots[2].amount = 81;
        minecraft.getItemModelResolver()
                .appendItemLayers(state3.slots[0].item, Items.IRON_INGOT.getDefaultInstance(), ItemDisplayContext.GUI, null, null, 0);
        state3.slots[0].amount = 9;
        minecraft.getItemModelResolver()
                .appendItemLayers(state3.slots[1].item, Items.IRON_BLOCK.getDefaultInstance(), ItemDisplayContext.GUI, null, null, 0);
        state3.slots[1].amount = 1;

        state3.isLocked = true;
        state3.isVoiding = true;
        state3.upgrade = ModItems.T4_UPGRADE;
        state3.hasLimiter = true;

        try (var ignored = ExtendedDrawers.CONFIG.override(state.config())) {
            poseStack.translate(0.5, 0, 0);
            minecraft.getBlockEntityRenderDispatcher().submit(state1, poseStack, submitNodeCollector, new CameraRenderState());
            poseStack.translate(-1, 0, 0);
            minecraft.getBlockEntityRenderDispatcher().submit(state2, poseStack, submitNodeCollector, new CameraRenderState());
            poseStack.translate(-1, 0, 0);
            minecraft.getBlockEntityRenderDispatcher().submit(state3, poseStack, submitNodeCollector, new CameraRenderState());
        }

        poseStack.popPose();
    }

    @SuppressWarnings("deprecation")
    public static boolean areComponentsBound() {
        return Items.AIR.builtInRegistryHolder().areComponentsBound();
    }

    @Override
    protected String getTextureLabel() {
        return "drawer layout preview";
    }
}
