package io.github.mattidragon.extendeddrawers.extensions.client;

import io.github.mattidragon.extendeddrawers.extensions.client.renderer.EnderConnectorBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class ExtendedDrawersExtensionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ExtensionBlocks.ENDER_CONNECTOR_ENTITY, EnderConnectorBlockEntityRenderer::new);
    }
}
