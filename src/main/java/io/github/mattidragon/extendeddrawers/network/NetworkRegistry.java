package io.github.mattidragon.extendeddrawers.network;


import com.kneelawk.graphlib.api.graph.GraphUniverse;
import com.kneelawk.graphlib.api.node.BlockNode;
import com.kneelawk.graphlib.api.node.BlockNodeDecoder;
import com.kneelawk.graphlib.api.node.BlockNodeDiscovery;
import com.kneelawk.graphlib.api.node.NodeKeyExtra;
import com.kneelawk.graphlib.api.wire.WireConnectionFilter;
import com.kneelawk.graphlib.api.world.SaveMode;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import io.github.mattidragon.extendeddrawers.network.node.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NetworkRegistry {
    public static final WireConnectionFilter CONNECTION_FILTER = (self, other) -> other instanceof DrawerNetworkBlockNode;
    public static final GraphUniverse UNIVERSE = GraphUniverse.builder()
            .saveMode(SaveMode.INCREMENTAL)
            .build(ExtendedDrawers.id("drawers"));

    public static void register() {
        UNIVERSE.register();
        UNIVERSE.addDiscoverer((world, pos) -> {
            if (world.getBlockState(pos).getBlock() instanceof NetworkComponent component) {
                var node = component.getNode();
                return List.of(new BlockNodeDiscovery(node, () -> node));
            }
            return List.of();
        });
        
        UNIVERSE.addDecoder(DrawerBlockNode.ID, new NodeDecoder(DrawerBlockNode.INSTANCE));
        UNIVERSE.addDecoder(ShadowDrawerBlockNode.ID, new NodeDecoder(ShadowDrawerBlockNode.INSTANCE));
        UNIVERSE.addDecoder(AccessPointBlockNode.ID, new NodeDecoder(AccessPointBlockNode.INSTANCE));
        UNIVERSE.addDecoder(ConnectorBlockNode.ID, new NodeDecoder(ConnectorBlockNode.INSTANCE));
        UNIVERSE.addDecoder(CompactingDrawerBlockNode.ID, new NodeDecoder(CompactingDrawerBlockNode.INSTANCE));

        ServerTickEvents.END_WORLD_TICK.register(UpdateHandler::flushUpdates);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            NetworkStorageCache.clear();
            UpdateHandler.clear();
        });
    }
    
    private record NodeDecoder(DrawerNetworkBlockNode instance) implements BlockNodeDecoder {
        @Override
        public @Nullable BlockNode createBlockNodeFromTag(@Nullable NbtElement tag) {
            return instance;
        }

        @Override
        public @Nullable NodeKeyExtra createKeyExtraFromTag(@Nullable NbtElement tag) {
            return instance;
        }
    }
}
