package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.client.renderer.state.ShadowDrawerRenderState;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShadowDrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<ShadowDrawerBlockEntity, ShadowDrawerRenderState> {
    public ShadowDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public ShadowDrawerRenderState createRenderState() {
        return new ShadowDrawerRenderState();
    }

    @Override
    public void updateRenderState(ShadowDrawerBlockEntity drawer, ShadowDrawerRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        super.updateRenderState(drawer, state, tickProgress, cameraPos, crumblingOverlay);
        state.isHidden = drawer.isHidden();
        state.count = drawer.countCache;
        itemModelManager.update(state.item, drawer.item.toStack(), ItemDisplayContext.GUI, drawer.getWorld(), null, drawer.getPos().hashCode());
    }

    @Override
    public void render(ShadowDrawerRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        var horizontalDir = state.blockState.get(StorageDrawerBlock.FACING);
        var face = state.blockState.get(StorageDrawerBlock.FACE);

        matrices.push();
        alignMatrices(matrices, horizontalDir, face);

        @Nullable
        String amount = String.valueOf(state.count);
        if (state.count == ShadowDrawerBlockEntity.INFINITE_COUNT_MARKER)
            amount = "∞";
        if (state.item.isEmpty())
            amount = null;
        if (state.count == 0 && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;

        var config = ExtendedDrawers.CONFIG.get().client().icons();
        @SuppressWarnings("deprecation")
        var blockAtlas = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
        var icons = state.isHidden
                ? List.of(new SpriteIdentifier(blockAtlas, config.hiddenIcon()))
                : List.<SpriteIdentifier>of();

        renderSlot(state.item, amount, false, state.isHidden, icons, matrices, queue, cameraState, state.lightmapCoordinates, state.pos);
        matrices.pop();
    }

    @Override
    public int getRenderDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.textRenderDistance(), config.itemRenderDistance());
    }
}
