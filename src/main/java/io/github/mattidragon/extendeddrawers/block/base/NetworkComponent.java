package io.github.mattidragon.extendeddrawers.block.base;

import com.google.common.collect.Queues;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.drawer.DrawerSlot;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;
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
    
    static List<DrawerSlot> getConnectedStorages(World world, BlockPos pos) {
        return findAllDrawers(world, pos).stream()
                .map(world::getBlockEntity)
                .map(DrawerBlockEntity.class::cast)
                .filter(Objects::nonNull)
                .flatMap(drawer -> Arrays.stream(drawer.storages))
                .sorted()
                .toList();
    }
    
    static List<BlockPos> findAllDrawers(World world, BlockPos pos) {
        return NetworkComponent.findConnectedComponents(world, pos, (world1, pos1) -> world1.getBlockState(pos1).getBlock() instanceof DrawerBlock);
    }
}
