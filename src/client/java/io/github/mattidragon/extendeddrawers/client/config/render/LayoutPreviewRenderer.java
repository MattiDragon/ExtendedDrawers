package io.github.mattidragon.extendeddrawers.client.config.render;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.client.renderer.AbstractDrawerBlockEntityRenderer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class LayoutPreviewRenderer extends SpecialGuiElementRenderer<LayoutPreviewRenderState> {
    public LayoutPreviewRenderer(VertexConsumerProvider.Immediate vertexConsumers) {
        super(vertexConsumers);
    }

    @Override
    public Class<LayoutPreviewRenderState> getElementClass() {
        return LayoutPreviewRenderState.class;
    }

    @Override
    protected void render(LayoutPreviewRenderState state, MatrixStack matrices) {
        var renderer = AbstractDrawerBlockEntityRenderer.createRendererTool();

        @SuppressWarnings("deprecation")
        var atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        var player = MinecraftClient.getInstance().player;
        var playerPos = player == null ? BlockPos.ORIGIN : player.getBlockPos();

        var voidingSprite = atlas.apply(Identifier.ofVanilla("item/lava_bucket"));
        var lockSprite = atlas.apply(id("item/lock"));
        var upgrade2Sprite = atlas.apply(id("item/t2_upgrade"));
        var upgrade4Sprite = atlas.apply(id("item/t4_upgrade"));

        matrices.push();
        matrices.scale(state.size(), state.size(), state.size());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrices.translate(-1, 0.5, 0);

        try (var ignored = ExtendedDrawers.CONFIG.override(state.config())) {
            renderer.renderSlot(ItemVariant.of(Items.COBBLESTONE), String.valueOf((Long) 128L), false, false, List.of(lockSprite), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);

            matrices.translate(0.75f, 0.25f, 0f);
            renderer.renderSlot(ItemVariant.of(Items.REDSTONE), String.valueOf((Long) 16L), true, false, List.of(lockSprite), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
            matrices.translate(0.5f, 0f, 0f);
            renderer.renderSlot(ItemVariant.of(Items.GUNPOWDER), String.valueOf((Long) 32L), true, false, List.of(voidingSprite), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
            matrices.translate(-0.5f, -0.5f, 0f);
            renderer.renderSlot(ItemVariant.of(Items.SUGAR), String.valueOf((Long) 64L), true, false, List.of(lockSprite, voidingSprite, upgrade2Sprite), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
            matrices.translate(0.5f, 0f, 0f);
            renderer.renderSlot(ItemVariant.of(Items.GLOWSTONE_DUST), String.valueOf((Long) 128L), true, false, List.of(upgrade4Sprite), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);

            matrices.translate(0.75f, 0.5f, 0f);
            renderer.renderIcons(List.of(lockSprite, voidingSprite, upgrade4Sprite), true, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
            renderer.renderSlot(ItemVariant.of(Items.IRON_INGOT), String.valueOf((Long) 9L), true, false, List.of(), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
            matrices.translate(0.25f, -0.5f, 0f);
            renderer.renderSlot(ItemVariant.of(Items.IRON_NUGGET), String.valueOf((Long) 81L), true, false, List.of(), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
            matrices.translate(-0.5f, 0f, 0f);
            renderer.renderSlot(ItemVariant.of(Items.IRON_BLOCK), String.valueOf((Long) 1L), true, false, List.of(), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, 0, playerPos, null);
        }

        matrices.pop();
    }

    @Override
    protected String getName() {
        return "drawer layout preview";
    }
}
