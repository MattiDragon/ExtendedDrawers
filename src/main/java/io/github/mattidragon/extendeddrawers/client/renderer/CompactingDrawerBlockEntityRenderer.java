package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.block.CompactingDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.config.ClientConfig;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

@SuppressWarnings("UnstableApiUsage")
public class CompactingDrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<CompactingDrawerBlockEntity> {
    public CompactingDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }
    
    @Override
    public int getRenderDistance() {
        var config = ClientConfig.HANDLE.get();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }
    
    @Override
    public void render(CompactingDrawerBlockEntity drawer, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var drawerPos = drawer.getPos();
        var dir = drawer.getCachedState().get(DrawerBlock.FACING);

        if (!shouldRender(drawer, dir)) return;

        matrices.push();
        alignMatrices(matrices, dir);

        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(drawer.getWorld()), drawer.getPos().offset(dir));

        renderIcons(drawer, matrices, vertexConsumers, light, overlay);

        var slots = drawer.storage.getActiveSlots();

        matrices.scale(0.5f, 0.5f, 1);
        if (slots.length >= 1) { // Top slot
            matrices.translate(0, 0.5, 0);
            renderSlot(drawer.storage.getSlot(CompactingDrawerBlock.getSlot(new Vec2f(0.5f, 0.25f), slots.length)), light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos);
        }
        if (slots.length >= 2) { // Bottom right
            matrices.translate(0.5, -1, 0);
            renderSlot(drawer.storage.getSlot(CompactingDrawerBlock.getSlot(new Vec2f(0.75f, 0.75f), slots.length)), light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos);
        }
        if (slots.length >= 3) { // Bottom left
            matrices.translate(-1, 0, 0);
            renderSlot(drawer.storage.getSlot(CompactingDrawerBlock.getSlot(new Vec2f(0.25f, 0.75f), slots.length)), light, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos);
        }

        
        matrices.pop();
    }

    private void renderIcons(CompactingDrawerBlockEntity drawer, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var icons = new ArrayList<Sprite>();
        var blockAtlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        if (drawer.storage.isLocked()) icons.add(blockAtlas.apply(id("item/lock")));
        if (drawer.storage.isVoiding()) icons.add(blockAtlas.apply(new Identifier("minecraft", "item/lava_bucket")));
        if (drawer.storage.isHidden()) icons.add(blockAtlas.apply(new Identifier("minecraft", "item/black_dye")));
        if (drawer.storage.getUpgrade() != null) icons.add(blockAtlas.apply(drawer.storage.getUpgrade().sprite));

        var player = MinecraftClient.getInstance().player;
        var playerPos = player == null ? Vec3d.ofCenter(drawer.getPos()) : player.getPos();

        if (drawer.getPos().isWithinDistance(playerPos, ClientConfig.HANDLE.get().iconRenderDistance())) {
            matrices.push(); // Render icons like the top slot
            matrices.scale(0.5f, 0.5f, 1);
            matrices.translate(0, 0.5, 0);
            renderIcons(icons, light, matrices, vertexConsumers, overlay);
            matrices.pop();
        }
    }

    private void renderSlot(CompactingDrawerStorage.Slot slot, int light, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, BlockPos pos) {
        if (slot.isBlocked()) return;

        var item = slot.getStorage().isHidden() ? ItemVariant.blank() : slot.getResource();
        @Nullable
        var amount = ((slot.getAmount() == 0) || ClientConfig.HANDLE.get().displayEmptyCount()) ? null : slot.getAmount();
        //noinspection ConstantConditions
        var playerPos = MinecraftClient.getInstance().player.getPos();
        var config = ClientConfig.HANDLE.get();

        if (pos.isWithinDistance(playerPos, config.textRenderDistance()) && amount != null)
            renderText(amount, light, matrices, vertexConsumers);
        if (pos.isWithinDistance(playerPos, config.itemRenderDistance()))
            renderItem(item, light, matrices, vertexConsumers, seed);
    }
}
