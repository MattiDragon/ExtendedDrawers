package io.github.mattidragon.extendeddrawers.client;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.client.compression.ClientCompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.client.config.render.LayoutPreviewRenderer;
import io.github.mattidragon.extendeddrawers.client.renderer.CompactingDrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.client.renderer.DrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.client.renderer.ShadowDrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.networking.CompressionRecipeSyncPayload;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ExtendedDrawersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlocks.DRAWER_BLOCK_ENTITY, DrawerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY, ShadowDrawerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY, CompactingDrawerBlockEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(CompressionRecipeSyncPayload.ID, (packet, context) ->
                ClientCompressionRecipeManager.of(context.player().networkHandler).addLadders(packet.recipes()));

        SpecialGuiElementRegistry.register(ctx -> new LayoutPreviewRenderer(ctx.vertexConsumers()));

        ExtendedDrawers.SHIFT_ACCESS = Screen::hasShiftDown;
    }
}
