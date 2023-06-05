package io.github.mattidragon.extendeddrawers.client.renderer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.CompactingDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

@SuppressWarnings("UnstableApiUsage")
public class CompactingDrawerBlockEntityRenderer extends AbstractDrawerBlockEntityRenderer<CompactingDrawerBlockEntity> {
    public CompactingDrawerBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        super(context.getItemRenderer(), context.getTextRenderer());
    }
    
    @Override
    public int getRenderDistance() {
        var config = ExtendedDrawers.CONFIG.get().client();
        return Math.max(config.iconRenderDistance(), Math.max(config.textRenderDistance(), config.itemRenderDistance()));
    }
    
    @Override
    public void render(CompactingDrawerBlockEntity drawer, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var drawerPos = drawer.getPos();
        var dir = drawer.getCachedState().get(DrawerBlock.FACING);
        var world = drawer.getWorld();

        if (!shouldRender(drawer, dir)) return;

        matrices.push();
        alignMatrices(matrices, dir);

        light = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(drawer.getWorld()), drawer.getPos().offset(dir));

        renderIcons(drawer, matrices, vertexConsumers, light, overlay);

        var slots = drawer.storage.getActiveSlots();

        if (slots.length >= 1) { // Top slot
            matrices.translate(0, 0.25, 0);
            renderSlot(drawer.storage.getSlot(CompactingDrawerBlock.getSlot(new Vec2f(0.5f, 0.25f), slots.length)), light, overlay, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos, world);
        }
        if (slots.length >= 2) { // Bottom right
            matrices.translate(0.25, -0.5, 0);
            renderSlot(drawer.storage.getSlot(CompactingDrawerBlock.getSlot(new Vec2f(0.75f, 0.75f), slots.length)), light, overlay, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos, world);
        }
        if (slots.length >= 3) { // Bottom left
            matrices.translate(-0.5, 0, 0);
            renderSlot(drawer.storage.getSlot(CompactingDrawerBlock.getSlot(new Vec2f(0.25f, 0.75f), slots.length)), light, overlay, matrices, vertexConsumers, (int) drawer.getPos().asLong(), drawerPos, world);
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

        if (drawer.getPos().isWithinDistance(playerPos, ExtendedDrawers.CONFIG.get().client().iconRenderDistance())) {
            matrices.push(); // Render icons like the top slot
            matrices.translate(0, 0.25, 0);
            renderIcons(icons, true, light, overlay, matrices, vertexConsumers);
            matrices.pop();
        }
    }

    private void renderSlot(CompactingDrawerStorage.Slot slot, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed, BlockPos pos, World world) {
        if (slot.isBlocked()) return;

        var item = slot.getStorage().isHidden() ? ItemVariant.blank() : slot.getResource();
        @Nullable
        var amount = ((slot.getAmount() == 0) || ExtendedDrawers.CONFIG.get().client().displayEmptyCount()) ? null : slot.getAmount();

        renderSlot(item, amount, true, List.of(), matrices, vertexConsumers, light, overlay, seed, pos, world);
    }
}
