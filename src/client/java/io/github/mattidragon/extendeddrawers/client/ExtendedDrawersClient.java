package io.github.mattidragon.extendeddrawers.client;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.ModBlocks;
import io.github.mattidragon.extendeddrawers.client.compression.ClientCompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.client.config.render.LayoutPreviewRenderer;
import io.github.mattidragon.extendeddrawers.client.renderer.CompactingDrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.client.renderer.DrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.client.renderer.ShadowDrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.networking.CompressionRecipeSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ExtendedDrawersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ModBlocks.DRAWER_BLOCK_ENTITY, DrawerBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY, ShadowDrawerBlockEntityRenderer::new);
        BlockEntityRenderers.register(ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY, CompactingDrawerBlockEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(CompressionRecipeSyncPayload.ID, (packet, context) ->
                ClientCompressionRecipeManager.of(context.player().connection).addLadders(packet.recipes()));

        PictureInPictureRendererRegistry.register(_ -> new LayoutPreviewRenderer());

        ExtendedDrawers.SHIFT_ACCESS = Minecraft.getInstance()::hasShiftDown;
    }
}
