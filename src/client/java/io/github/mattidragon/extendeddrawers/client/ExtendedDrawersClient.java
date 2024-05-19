package io.github.mattidragon.extendeddrawers.client;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.client.renderer.CompactingDrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.client.renderer.DrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.client.renderer.ShadowDrawerBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.compacting.CompressionRecipeManager;
import io.github.mattidragon.extendeddrawers.networking.CompressionOverrideSyncPayload;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ExtendedDrawersClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlocks.DRAWER_BLOCK_ENTITY, DrawerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY, ShadowDrawerBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY, CompactingDrawerBlockEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(CompressionOverrideSyncPayload.ID, (packet, context) ->
                CompressionRecipeManager.of(context.player().networkHandler.getRecipeManager()).setOverrides(packet.overrides()));

        ExtendedDrawers.SHIFT_ACCESS = Screen::hasShiftDown;
    }
}
