package io.github.mattidragon.extendeddrawers.extensions.component;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class ExtensionDataComponents {
    public static final DataComponentType<LinkingEnderConnectorComponent> LINKING_ENDER_CONNECTOR = DataComponentType.<LinkingEnderConnectorComponent>builder()
            .persistent(LinkingEnderConnectorComponent.CODEC)
            .networkSynchronized(LinkingEnderConnectorComponent.PACKET_CODEC)
            .build();

    private ExtensionDataComponents() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("linking_ender_connector"), LINKING_ENDER_CONNECTOR);
    }
}
