package io.github.mattidragon.extendeddrawers.extensions;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.extensions.network.link.EnderConnectorLinkKey;
import io.github.mattidragon.extendeddrawers.extensions.network.node.DrawerBarrelBlockNode;
import io.github.mattidragon.extendeddrawers.extensions.network.node.EnderConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionDataComponents;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionItems;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedDrawersExtensions implements ModInitializer {
    public static final String MOD_ID = "extended_drawers_extensions";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ExtensionBlocks.register();
        ExtensionItems.register();
        ExtensionDataComponents.register();

        BlockEntityType.BARREL.addSupportedBlock(ExtensionBlocks.DRAWER_BARREL);

        ItemGroupEvents.modifyEntriesEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, ExtendedDrawers.id("main")))
                .register(entries -> {
                    entries.addAfter(ModItems.ACCESS_POINT, ExtensionItems.DRAWER_BARREL, ExtensionItems.ENDER_CONNECTOR);
                    entries.addAfter(ModItems.DUPE_WAND, ExtensionItems.ENDER_CONNECTOR_LINKER);
                });
        NetworkRegistry.UNIVERSE.addNodeTypes(DrawerBarrelBlockNode.TYPE, EnderConnectorBlockNode.TYPE);
        NetworkRegistry.UNIVERSE.addLinkKeyType(EnderConnectorLinkKey.TYPE);

        ComponentTooltipAppenderRegistry.addBefore(DataComponents.LORE, ExtensionDataComponents.LINKING_ENDER_CONNECTOR);
    }
}
