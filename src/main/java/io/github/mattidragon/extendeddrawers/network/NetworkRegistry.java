package io.github.mattidragon.extendeddrawers.network;

import com.kneelawk.graphlib.GraphLib;
import com.kneelawk.graphlib.wire.WireConnectionFilter;
import io.github.mattidragon.extendeddrawers.network.node.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.registry.Registry;

public class NetworkRegistry {
    public static final WireConnectionFilter CONNECTION_FILTER = (self, other) -> other instanceof AbstractDrawerBlockNode;
    
    public static void register() {
        GraphLib.registerDiscoverer(new DrawerNodeDiscoverer());
        Registry.register(GraphLib.BLOCK_NODE_DECODER, DrawerBlockNode.ID, DrawerBlockNode.DECODER);
        Registry.register(GraphLib.BLOCK_NODE_DECODER, ShadowDrawerBlockNode.ID, ShadowDrawerBlockNode.DECODER);
        Registry.register(GraphLib.BLOCK_NODE_DECODER, AccessPointBlockNode.ID, AccessPointBlockNode.DECODER);
        Registry.register(GraphLib.BLOCK_NODE_DECODER, ConnectorBlockNode.ID, ConnectorBlockNode.DECODER);
    
        ServerTickEvents.END_WORLD_TICK.register(UpdateHandler::flushUpdates);
    }
}
