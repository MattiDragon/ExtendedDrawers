package io.github.mattidragon.extendeddrawers.network;

import com.kneelawk.graphlib.api.graph.GraphUniverse;
import com.kneelawk.graphlib.api.graph.user.GraphEntityType;
import com.kneelawk.graphlib.api.world.SaveMode;
import com.mojang.serialization.Codec;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import io.github.mattidragon.extendeddrawers.network.cache.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.network.node.*;

import java.util.List;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class NetworkRegistry {
    public static final GraphUniverse UNIVERSE = GraphUniverse.builder()
            .saveMode(SaveMode.INCREMENTAL)
            .build(id("drawers"));
    public static final GraphEntityType<NetworkStorageCache> STORAGE_CACHE_TYPE = GraphEntityType.of(id("storage_cache"),
            Codec.unit(() -> ExtendedDrawers.CONFIG.get().misc().cachingMode().createCache()),
            () -> ExtendedDrawers.CONFIG.get().misc().cachingMode().createCache(),
            NetworkStorageCache::split);
    public static final GraphEntityType<UpdateHandler> UPDATE_HANDLER_TYPE = GraphEntityType.of(id("update_handler"), UpdateHandler::new);

    public static void register() {
        UNIVERSE.register();
        UNIVERSE.addDiscoverer((world, pos) -> {
            if (world.getBlockState(pos).getBlock() instanceof NetworkComponent component) {
                return List.of(component.getNode());
            }
            return List.of();
        });

        UNIVERSE.addGraphEntityTypes(STORAGE_CACHE_TYPE, UPDATE_HANDLER_TYPE);
        UNIVERSE.addNodeTypes(DrawerBlockNode.TYPE, ShadowDrawerBlockNode.TYPE, AccessPointBlockNode.TYPE, ConnectorBlockNode.TYPE, CompactingDrawerBlockNode.TYPE);
    }
}
