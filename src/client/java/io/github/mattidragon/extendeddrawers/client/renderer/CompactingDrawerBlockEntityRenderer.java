package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompactingDrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<CompactingDrawerBlockEntity> {
    public CompactingDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context.getItemModelManager(), context.getTextRenderer());
    }

    @Override
    public int getRenderDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }

    @Override
    public void render(CompactingDrawerBlockEntity drawer, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        var drawerPos = drawer.getPos();
        var horizontalDir = drawer.getCachedState().get(StorageDrawerBlock.FACING);
        var face = drawer.getCachedState().get(StorageDrawerBlock.FACE);
        var dir = StorageDrawerBlock.getFront(drawer.getCachedState());
        var world = drawer.getWorld();

        if (!shouldRender(drawer, dir)) return;

        matrices.push();
        alignMatrices(matrices, horizontalDir, face);

        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(drawer.getWorld()), drawer.getPos().offset(dir));

        if (drawer.storage.isHidden()) {
            renderHiddenOverlay(false, light, overlay, matrices, vertexConsumers);
            matrices.pop();
            return;
        }

        renderIcons(drawer, matrices, vertexConsumers, light, overlay);

        var slots = drawer.storage.getActiveSlotArray();

        if (slots.length >= 1) { // Top slot
            matrices.translate(0, 0.25, 0);
            renderSlot(ModBlocks.COMPACTING_DRAWER.getSlot(drawer, ModBlocks.COMPACTING_DRAWER.getSlotIndex(drawer, new Vec2f(0.5f, 0.25f))), light, overlay, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos, world);
        }
        if (slots.length >= 2) { // Bottom right
            matrices.translate(0.25, -0.5, 0);
            renderSlot(ModBlocks.COMPACTING_DRAWER.getSlot(drawer, ModBlocks.COMPACTING_DRAWER.getSlotIndex(drawer, new Vec2f(0.75f, 0.75f))), light, overlay, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos, world);
        }
        if (slots.length >= 3) { // Bottom left
            matrices.translate(-0.5, 0, 0);
            renderSlot(ModBlocks.COMPACTING_DRAWER.getSlot(drawer, ModBlocks.COMPACTING_DRAWER.getSlotIndex(drawer, new Vec2f(0.25f, 0.75f))), light, overlay, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos, world);
        }
        
        matrices.pop();
    }

    private void renderIcons(CompactingDrawerBlockEntity drawer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var icons = new ArrayList<Sprite>();
        var config = ExtendedDrawers.CONFIG.get().client().icons();
        @SuppressWarnings("deprecation")
        var blockAtlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        if (drawer.storage.isLocked()) icons.add(blockAtlas.apply(config.lockedIcon()));
        if (drawer.storage.isVoiding()) icons.add(blockAtlas.apply(config.voidingIcon()));
        if (drawer.storage.isHidden()) icons.add(blockAtlas.apply(config.hiddenIcon()));
        if (drawer.storage.isDuping()) icons.add(blockAtlas.apply(config.dupingIcon()));
        if (drawer.storage.getUpgrade() != null) icons.add(blockAtlas.apply(drawer.storage.getUpgrade().sprite));
        if (drawer.storage.hasLimiter()) icons.add(blockAtlas.apply(ExtendedDrawers.id("item/limiter")));

        var player = MinecraftClient.getInstance().player;
        var playerPos = player == null ? Vec3d.ofCenter(drawer.getPos()) : player.getPos();

        if (drawer.getPos().isWithinDistance(playerPos, ExtendedDrawers.CONFIG.get().client().iconRenderDistance())) {
            matrices.push(); // Render icons like the top slot
            matrices.translate(0, 0.25, 0);
            renderIcons(icons, true, light, overlay, matrices, vertexConsumers);
            matrices.pop();
        }
    }

    private void renderSlot(CompactingDrawerStorage.Slot slot, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, BlockPos pos, World world) {
        if (slot.isBlocked()) return;

        @Nullable
        String amount = String.valueOf(slot.getAmount());
        if ((slot.getAmount() == 0) && !ExtendedDrawers.CONFIG.get().client().displayEmptyCount())
            amount = null;
        if (slot.getStorage().isDuping())
            amount = "∞";

        var item = slot.getStorage().isHidden() ? ItemVariant.blank() : slot.getResource();
        renderSlot(item, amount, true, false, List.of(), matrices, vertexConsumers, light, overlay, seed, pos, world);
    }
}
