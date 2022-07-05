package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.config.ClientConfig;
import io.github.mattidragon.extendeddrawers.drawer.DrawerSlot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.Objects;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class DrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<DrawerBlockEntity> {
    public DrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }
    
    @Override
    public int getRenderDistance() {
        var config = ClientConfig.HANDLE.get();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }
    
    @Override
    public void render(DrawerBlockEntity drawer, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var dir = drawer.getCachedState().get(DrawerBlock.FACING);
        var pos = dir.getUnitVector();
        if (!shouldRender(drawer, dir)) return;
        
        matrices.push();
        matrices.translate(pos.getX() / 2 + 0.5, pos.getY() / 2 + 0.5, pos.getZ() / 2 + 0.5);
        matrices.multiply(dir.getRotationQuaternion());
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
        matrices.translate(0, 0, 0.01);
    
        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(drawer.getWorld()), drawer.getPos().offset(dir));
        var slots = ((DrawerBlock)drawer.getCachedState().getBlock()).slots;
        var blockPos = drawer.getPos();
        
        switch (slots) {
            case 1 -> renderSlot(drawer.storages[0], light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos);
            case 2 -> {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.5, 0, 0);
                renderSlot(drawer.storages[0], light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(drawer.storages[1], light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos);
            }
            case 4 -> {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.5, 0.5, 0);
                renderSlot(drawer.storages[0], light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(drawer.storages[1], light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos);
                matrices.translate(-1, -1, 0);
                renderSlot(drawer.storages[2], light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(drawer.storages[3], light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), overlay, blockPos);
            }
            default -> ExtendedDrawers.LOGGER.error("Unexpected drawer slot count, skipping rendering. Are you an addon dev adding more configurations? If so please mixin into DrawerBlockEntityRenderer and add your layout.");
        }
        
        matrices.pop();
    }
    
    private void renderSlot(DrawerSlot storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, int overlay, BlockPos pos) {
        var icons = new ArrayList<Sprite>();
        var blockAtlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        
        if (storage.isLocked()) icons.add(blockAtlas.apply(id("item/lock")));
        if (storage.isVoiding()) icons.add(blockAtlas.apply(new Identifier("minecraft", "item/lava_bucket")));
        if (storage.getUpgrade() != null) icons.add(blockAtlas.apply(storage.getUpgrade().sprite));
        
        renderSlot(storage.getItem(), ((storage.getAmount() == 0) || ClientConfig.HANDLE.get().displayEmptyCount()) ? null : storage.getAmount(), icons, matrices, vertexConsumers, light, overlay, seed, pos);
    }
}
