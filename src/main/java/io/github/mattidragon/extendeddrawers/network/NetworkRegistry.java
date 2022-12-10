package io.github.mattidragon.extendeddrawers.network;

import com.kneelawk.graphlib.GraphLib;
import com.kneelawk.graphlib.wire.WireConnectionFilter;
import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import io.github.mattidragon.extendeddrawers.network.node.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.Registry;

import java.util.List;

public class NetworkRegistry {
    public static final WireConnectionFilter CONNECTION_FILTER = (self, other) -> other instanceof AbstractDrawerBlockNode;
    
    public static void register() {
        GraphLib.registerDiscoverer((world, pos) -> {
            if (world.getBlockState(pos).getBlock() instanceof NetworkComponent component) {
                return component.createNodes();
            }
            return List.of();
        });
        Registry.register(GraphLib.BLOCK_NODE_DECODER, DrawerBlockNode.ID, DrawerBlockNode.DECODER);
        Registry.register(GraphLib.BLOCK_NODE_DECODER, ShadowDrawerBlockNode.ID, ShadowDrawerBlockNode.DECODER);
        Registry.register(GraphLib.BLOCK_NODE_DECODER, AccessPointBlockNode.ID, AccessPointBlockNode.DECODER);
        Registry.register(GraphLib.BLOCK_NODE_DECODER, ConnectorBlockNode.ID, ConnectorBlockNode.DECODER);
    
        ServerTickEvents.END_WORLD_TICK.register(UpdateHandler::flushUpdates);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            NetworkStorageCache.clear();
            UpdateHandler.clear();
        });
    }
}
