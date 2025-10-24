package io.github.mattidragon.extendeddrawers.extensions.registry;

import io.github.mattidragon.extendeddrawers.extensions.component.LinkingEnderConnectorComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class ExtensionDataComponents {
    public static final ComponentType<LinkingEnderConnectorComponent> LINKING_ENDER_CONNECTOR = ComponentType.<LinkingEnderConnectorComponent>builder()
            .codec(LinkingEnderConnectorComponent.CODEC)
            .packetCodec(LinkingEnderConnectorComponent.PACKET_CODEC)
            .build();

    private ExtensionDataComponents() {
    }

    public static void register() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, id("linking_ender_connector"), LINKING_ENDER_CONNECTOR);
    }
}
