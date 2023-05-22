package io.github.mattidragon.extendeddrawers.network;


import com.kneelawk.graphlib.api.graph.GraphUniverse;
import com.kneelawk.graphlib.api.node.BlockNodeDiscovery;
import com.kneelawk.graphlib.api.node.NodeKey;
import com.kneelawk.graphlib.api.util.SimpleNodeKey;
import com.kneelawk.graphlib.api.wire.WireConnectionFilter;
import com.kneelawk.graphlib.api.world.SaveMode;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import io.github.mattidragon.extendeddrawers.network.node.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.List;

public class NetworkRegistry {
    public static final WireConnectionFilter CONNECTION_FILTER = (self, other) -> other instanceof DrawerNetworkBlockNode;
    public static final NodeKey KEY = new SimpleNodeKey(ExtendedDrawers.id("center"));
    public static final GraphUniverse UNIVERSE = GraphUniverse.builder()
            .saveMode(SaveMode.INCREMENTAL)
            .build(ExtendedDrawers.id("drawers"));

    public static void register() {
        UNIVERSE.register();
        UNIVERSE.addDiscoverer((world, pos) -> {
            if (world.getBlockState(pos).getBlock() instanceof NetworkComponent component) {
                return List.of(new BlockNodeDiscovery(KEY, component::getNode));
            }
            return List.of();
        });

        UNIVERSE.addNodeKeyDecoder(KEY.getTypeId(), tag -> KEY);

        UNIVERSE.addNodeDecoder(DrawerBlockNode.ID, (tag, ctx) -> new DrawerBlockNode(ctx));
        UNIVERSE.addNodeDecoder(ShadowDrawerBlockNode.ID, (tag, ctx) -> new ShadowDrawerBlockNode(ctx));
        UNIVERSE.addNodeDecoder(AccessPointBlockNode.ID, (tag, ctx) -> new AccessPointBlockNode(ctx));
        UNIVERSE.addNodeDecoder(ConnectorBlockNode.ID, (tag, ctx) -> new ConnectorBlockNode(ctx));
        UNIVERSE.addNodeDecoder(CompactingDrawerBlockNode.ID, (tag, ctx) -> new CompactingDrawerBlockNode(ctx));

        ServerTickEvents.END_WORLD_TICK.register(UpdateHandler::flushUpdates);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            NetworkStorageCache.clear();
            UpdateHandler.clear();
        });
    }
}
