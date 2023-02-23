package io.github.mattidragon.extendeddrawers.network;

import com.kneelawk.graphlib.GraphLib;
import com.kneelawk.graphlib.graph.BlockNodeHolder;
import com.kneelawk.graphlib.graph.struct.Node;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class UpdateHandler {
    private static final Map<RegistryKey<World>, Long2ObjectMap<ChangeType>> UPDATES = new HashMap<>();

    public static void scheduleUpdate(ServerWorld world, BlockPos pos, ChangeType type) {
        GraphLib.getController(world).getGraphsAt(pos).forEach(graph -> scheduleUpdate(world, graph, type));
    }
    
    public static void scheduleUpdate(ServerWorld world, long id, ChangeType type) {
        var map = UPDATES.computeIfAbsent(world.getRegistryKey(), k -> new Long2ObjectOpenHashMap<>());
        map.compute(id, (__, old) -> ObjectUtils.max(old, type));
    }
    
    public static void flushUpdates(ServerWorld world) {
        var controller = GraphLib.getController(world);
        var profiler = world.getProfiler();
        profiler.push("extended_drawers:network_updates");
        var updates = UPDATES.remove(world.getRegistryKey());
        if (updates != null) {
            updates.forEach((id, type) -> {
                var graph = controller.getGraph(id);
                if (graph == null) return;
                NetworkStorageCache.update(world, id, type);
                updateGraph(world, graph.getNodes(), type);
            });
        }
        profiler.pop();
    }

    public static void clear() {
        UPDATES.clear();
    }

    private static void updateGraph(ServerWorld world, Stream<Node<BlockNodeHolder>> nodes, ChangeType type) {
        nodes.forEach(node -> {
            if (node.data().getNode() instanceof DrawerNetworkBlockNode drawerNode) {
                drawerNode.update(world, node, type);
            }
        });
    }
    
    public enum ChangeType {
        COUNT, // Need to send packets to client
        CONTENT, // Need to sort slots (includes lock changes)
        STRUCTURE // Need to rebuild storage list
    }
}
