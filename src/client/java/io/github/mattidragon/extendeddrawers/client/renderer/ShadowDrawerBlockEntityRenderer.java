package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.ShadowDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ShadowDrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<ShadowDrawerBlockEntity> {
    public ShadowDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context.getItemRenderer(), context.getTextRenderer());
    }
    
    @Override
    public int getRenderDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.textRenderDistance(), config.itemRenderDistance());
    }
    
    @Override
    public void render(ShadowDrawerBlockEntity drawer, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var dir = drawer.getCachedState().get(ShadowDrawerBlock.FACING);
        if (!shouldRender(drawer, dir)) return;
        
        matrices.push();
        alignMatrices(matrices, dir, BlockFace.WALL);

        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(drawer.getWorld()), drawer.getPos().offset(dir));

        List<Sprite> icons = drawer.isHidden() ? List.of(MinecraftClient.getInstance()
                .getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
                .apply(ExtendedDrawers.CONFIG.get().client().icons().hiddenIcon())) : List.of();
        @Nullable
        String amount = String.valueOf(drawer.countCache);
        if (drawer.countCache == ShadowDrawerBlockEntity.INFINITE_COUNT_MARKER)
            amount = "∞";
        if (drawer.item.isBlank())
            amount = null;
        if (drawer.countCache == 0 && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;

        renderSlot(drawer.item, amount, false, drawer.isHidden(), icons, matrices, vertexConsumers, light, overlay, (int) drawer.getPos().asLong(), drawer.getPos(), drawer.getWorld());
        matrices.pop();
    }
}
