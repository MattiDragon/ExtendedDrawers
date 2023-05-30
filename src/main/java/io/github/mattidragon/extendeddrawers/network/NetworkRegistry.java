package io.github.mattidragon.extendeddrawers.network;


import com.kneelawk.graphlib.api.graph.GraphUniverse;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import com.kneelawk.graphlib.api.wire.WireConnectionFilter;
import com.kneelawk.graphlib.api.world.SaveMode;
import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import io.github.mattidragon.extendeddrawers.network.node.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.List;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class NetworkRegistry {
    public static final WireConnectionFilter CONNECTION_FILTER = (self, other) -> other instanceof DrawerNetworkBlockNode;
    public static final GraphUniverse UNIVERSE = GraphUniverse.builder()
            .saveMode(SaveMode.INCREMENTAL)
            .build(id("drawers"));
    public static final GraphEntityType<NetworkStorageCache> STORAGE_CACHE_TYPE = new GraphEntityType<>(id("storage_cache"),
            NetworkStorageCache::new,
            (nbt, context) -> new NetworkStorageCache(context),
            (original, originalGraph, ctx) -> new NetworkStorageCache(ctx)); // No need to actually split as it's only a cache.
    public static final GraphEntityType<UpdateHandler> UPDATE_HANDLER_TYPE = new GraphEntityType<>(id("update_handler"),
            UpdateHandler::new,
            (nbt, context) -> new UpdateHandler(context),
            (original, originalGraph, ctx) -> new UpdateHandler(ctx)); // No need to actually split as it's only a cache.

    public static void register() {
        UNIVERSE.register();
        UNIVERSE.addDiscoverer((world, pos) -> {
            if (world.getBlockState(pos).getBlock() instanceof NetworkComponent component) {
                return List.of(component.getNode());
            }
            return List.of();
        });

        UNIVERSE.addGraphEntityTypes(STORAGE_CACHE_TYPE, UPDATE_HANDLER_TYPE);
        
        UNIVERSE.addNodeDecoder(DrawerBlockNode.ID, (tag) -> DrawerBlockNode.INSTANCE);
        UNIVERSE.addNodeDecoder(ShadowDrawerBlockNode.ID, (tag) -> ShadowDrawerBlockNode.INSTANCE);
        UNIVERSE.addNodeDecoder(AccessPointBlockNode.ID, (tag) -> AccessPointBlockNode.INSTANCE);
        UNIVERSE.addNodeDecoder(ConnectorBlockNode.ID, (tag) -> ConnectorBlockNode.INSTANCE);
        UNIVERSE.addNodeDecoder(CompactingDrawerBlockNode.ID, (tag) -> CompactingDrawerBlockNode.INSTANCE);

        ServerTickEvents.END_WORLD_TICK.register(UpdateHandler::flushUpdates);
    }
}
