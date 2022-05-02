package io.github.mattidragon.extendeddrawers.client.renderer;

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
    public void render(DrawerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var dir = entity.getCachedState().get(DrawerBlock.FACING);
        var pos = dir.getUnitVector();
    
        matrices.translate(pos.getX() / 2 + 0.5, pos.getY() / 2 + 0.5, pos.getZ() / 2 + 0.5);
        matrices.multiply(dir.getRotationQuaternion());
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
        matrices.translate(0, 0, 0.01);
    
        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(entity.getWorld()), entity.getPos().offset(dir));
        var slots = ((DrawerBlock)entity.getCachedState().getBlock()).slots;
        var blockPos = entity.getPos();
        
        switch (slots) {
            case 1 -> renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
            case 2 -> {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.5, 0, 0);
                renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[1], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
            }
            case 4 -> {
                matrices.scale(0.5f, 0.5f, 0.5f);
                matrices.translate(-0.5, 0.5, 0);
                renderSlot(entity.storages[0], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[1], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
                matrices.translate(-1, -1, 0);
                renderSlot(entity.storages[2], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
                matrices.translate(1, 0, 0);
                renderSlot(entity.storages[3], light, matrices, vertexConsumers, (int) entity.getPos().asLong(), overlay, blockPos);
            }
            default -> throw new IllegalStateException("unexpected drawer slot count");
        }
        
        matrices.pop();
    }
    
    private void renderSlot(DrawerSlot storage, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, int overlay, BlockPos pos) {
        var icons = new ArrayList<Sprite>();
        var blockAtlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        
        if (storage.locked) icons.add(blockAtlas.apply(id("item/lock")));
        if (storage.upgrade != null) icons.add(blockAtlas.apply(storage.upgrade.sprite));
        
        renderSlot(storage.item, storage.amount == 0 ? null: storage.amount, icons, matrices, vertexConsumers, light, overlay, seed, pos);
    }
}
