package io.github.mattidragon.extendeddrawers.block;

import com.google.common.collect.Queues;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface NetworkComponent {
    
    static Optional<BlockPos> findFirstConnectedComponent(World world, BlockPos pos, BiPredicate<World, BlockPos> predicate) {
        var toSearch = Queues.newArrayDeque(Collections.singleton(pos));
        var searched = new ArrayList<>();
    
        while (!toSearch.isEmpty()) {
            var searching = toSearch.poll();
            searched.add(searching);
        
            var state = world.getBlockState(searching);
            if (!(state.getBlock() instanceof NetworkComponent))
                continue;
            if (predicate.test(world, searching))
                return Optional.of(searching);
        
            for (var dir : Direction.values()) {
                var offsetPos = searching.offset(dir);
                if (!searched.contains(offsetPos) && offsetPos.isWithinDistance(pos, 64))  // TODO: config
                    toSearch.add(offsetPos);
            }
        }
    
        return Optional.empty();
    }
    
    static List<BlockPos> findConnectedComponents(World world, BlockPos pos, BiPredicate<World, BlockPos> predicate) {
        var toSearch = Queues.newArrayDeque(Collections.singleton(pos));
        var searched = new ArrayList<>();
        var found = new ArrayList<BlockPos>();
    
        while (!toSearch.isEmpty()) {
            var searching = toSearch.poll();
            searched.add(searching);
        
            var state = world.getBlockState(searching);
            if (!(state.getBlock() instanceof NetworkComponent))
                continue;
            if (predicate.test(world, searching))
                found.add(searching);
        
            for (var dir : Direction.values()) {
                var offsetPos = searching.offset(dir);
                if (!searched.contains(offsetPos) && offsetPos.isWithinDistance(pos, 64))  // TODO: config
                    toSearch.add(offsetPos);
            }
        }
    
        return found;
    }
}
