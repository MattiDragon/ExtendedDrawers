package io.github.mattidragon.extendeddrawers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.client.renderer.state.ShadowDrawerRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ShadowDrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<ShadowDrawerBlockEntity, ShadowDrawerRenderState> {
    public ShadowDrawerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ShadowDrawerRenderState createRenderState() {
        return new ShadowDrawerRenderState();
    }

    @Override
    public void extractRenderState(ShadowDrawerBlockEntity drawer, ShadowDrawerRenderState state, float tickProgress, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        super.extractRenderState(drawer, state, tickProgress, cameraPos, crumblingOverlay);
        state.isHidden = drawer.isHidden();
        state.count = drawer.countCache;
        itemModelManager.appendItemLayers(state.item, drawer.item.toStack(), ItemDisplayContext.GUI, drawer.getLevel(), null, drawer.getBlockPos().hashCode());
    }

    @Override
    public void submit(ShadowDrawerRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        var horizontalDir = state.blockState.getValue(StorageDrawerBlock.FACING);
        var face = state.blockState.getValue(StorageDrawerBlock.FACE);

        matrices.pushPose();
        alignMatrices(matrices, horizontalDir, face);

        String amount = String.valueOf(state.count);
        if (state.count == ShadowDrawerBlockEntity.INFINITE_COUNT_MARKER)
            amount = "∞";
        if (state.item.isEmpty())
            amount = null;
        if (state.count == 0 && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;

        var config = ExtendedDrawers.CONFIG.get().client().icons();
        @SuppressWarnings("deprecation")
        var atlas = TextureAtlas.LOCATION_ITEMS;
        var icons = state.isHidden
                ? List.of(new Material(atlas, config.hiddenIcon()))
                : List.<Material>of();

        renderSlot(state.item, amount, false, state.isHidden, icons, matrices, queue, cameraState, state.lightCoords, state.blockPos);
        matrices.popPose();
    }

    @Override
    public int getViewDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.textRenderDistance(), config.itemRenderDistance());
    }
}
