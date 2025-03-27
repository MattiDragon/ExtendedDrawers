package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.storage.DrawerSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Objects;

public class DrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<DrawerBlockEntity> {
    public DrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context.getItemModelManager(), context.getTextRenderer());
    }
    
    @Override
    public int getRenderDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }
    
    @Override
    public void render(DrawerBlockEntity drawer, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        var horizontalDir = drawer.getCachedState().get(StorageDrawerBlock.FACING);
        var face = drawer.getCachedState().get(StorageDrawerBlock.FACE);
        var dir = StorageDrawerBlock.getFront(drawer.getCachedState());
        var world = drawer.getWorld();

        if (!shouldRender(drawer, dir)) return;

        matrices.push();
        alignMatrices(matrices, horizontalDir, face);

        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(drawer.getWorld()), drawer.getPos().offset(dir));
        var slots = ((DrawerBlock)drawer.getCachedState().getBlock()).slots;
        var blockPos = drawer.getPos();
        
        switch (slots) {
            case 1 -> renderSlot(drawer.storages[0], false, light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos, world);
            case 2 -> {
                matrices.translate(-0.25, 0, 0);
                renderSlot(drawer.storages[0], true, light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos, world);
                matrices.translate(0.5, 0, 0);
                renderSlot(drawer.storages[1], true, light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos, world);
            }
            case 4 -> {
                matrices.translate(-0.25, 0.25, 0);
                renderSlot(drawer.storages[0], true, light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos, world);
                matrices.translate(0.5, 0, 0);
                renderSlot(drawer.storages[1], true, light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos, world);
                matrices.translate(-0.5, -0.5, 0);
                renderSlot(drawer.storages[2], true, light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos, world);
                matrices.translate(0.5, 0, 0);
                renderSlot(drawer.storages[3], true, light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos, world);
            }
            default -> ExtendedDrawers.LOGGER.error("Unexpected drawer slot count, skipping rendering. Are you an addon dev adding more configurations? If so please mixin into DrawerBlockEntityRenderer and add your layout.");
        }
        
        matrices.pop();
    }
    
    private void renderSlot(DrawerSlot storage, boolean small, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, int overlay, BlockPos pos, World world) {
        var icons = new ArrayList<Sprite>();
        var config = ExtendedDrawers.CONFIG.get().client().icons();
        @SuppressWarnings("deprecation")
        var blockAtlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        
        if (storage.isLocked()) icons.add(blockAtlas.apply(config.lockedIcon()));
        if (storage.isVoiding()) icons.add(blockAtlas.apply(config.voidingIcon()));
        if (storage.isHidden()) icons.add(blockAtlas.apply(config.hiddenIcon()));
        if (storage.isDuping()) icons.add(blockAtlas.apply(config.dupingIcon()));
        if (storage.getUpgrade() != null) icons.add(blockAtlas.apply(storage.getUpgrade().sprite));
        if (storage.hasLimiter()) icons.add(blockAtlas.apply(ExtendedDrawers.id("item/limiter")));

        String amount = String.valueOf(storage.getAmount());
        if ((storage.getAmount() == 0) && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;
        if (storage.isDuping())
            amount = "∞";

        renderSlot(storage.getResource(), amount, small, storage.isHidden(), icons, matrices, vertexConsumers, light, overlay, seed, pos, world);
    }
}
