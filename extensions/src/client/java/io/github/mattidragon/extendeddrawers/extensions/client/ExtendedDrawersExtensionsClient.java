package io.github.mattidragon.extendeddrawers.extensions.client;

import io.github.mattidragon.extendeddrawers.extensions.client.renderer.EnderConnectorBlockEntityRenderer;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ExtendedDrawersExtensionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ExtensionBlocks.ENDER_CONNECTOR_ENTITY, EnderConnectorBlockEntityRenderer::new);
    }
}
