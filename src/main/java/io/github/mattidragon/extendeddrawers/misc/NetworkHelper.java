package io.github.mattidragon.extendeddrawers.misc;

import com.google.common.collect.Queues;
import io.github.mattidragon.extendeddrawers.block.DrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import io.github.mattidragon.extendeddrawers.drawer.DrawerSlot;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiPredicate;

public class NetworkHelper {
    public static Optional<BlockPos> findFirstConnectedComponent(World world, BlockPos pos, BiPredicate<World, BlockPos> predicate) {
        var toSearch = Queues.newArrayDeque(Collections.singleton(pos));
        var searched = new ArrayList<>();
    
        while (!toSearch.isEmpty()) {
            var searching = toSearch.poll();
            searched.add(searching);
        
            var state = world.getBlockState(searching);
            if (!state.isIn(ModTags.BlockTags.NETWORK_COMPONENTS))
                continue;
            if (predicate.test(world, searching))
                return Optional.of(searching);
        
            for (var dir : Direction.values()) {
                var offsetPos = searching.offset(dir);
                if (!searched.contains(offsetPos) && offsetPos.isWithinDistance(pos, CommonConfig.HANDLE.get().networkSearchDistance()))
                    toSearch.add(offsetPos);
            }
        }
    
        return Optional.empty();
    }
    
    public static List<BlockPos> findConnectedComponents(World world, BlockPos pos, BiPredicate<World, BlockPos> predicate) {
        var toSearch = Queues.newArrayDeque(Collections.singleton(pos));
        var searched = new ArrayList<>();
        var found = new ArrayList<BlockPos>();
    
        while (!toSearch.isEmpty()) {
            var searching = toSearch.poll();
            searched.add(searching);
        
            var state = world.getBlockState(searching);
            if (!state.isIn(ModTags.BlockTags.NETWORK_COMPONENTS))
                continue;
            if (predicate.test(world, searching))
                found.add(searching);
        
            for (var dir : Direction.values()) {
                var offsetPos = searching.offset(dir);
                if (!searched.contains(offsetPos) && offsetPos.isWithinDistance(pos, CommonConfig.HANDLE.get().networkSearchDistance()))
                    toSearch.add(offsetPos);
            }
        }
    
        return found;
    }
    
    public static List<DrawerSlot> getConnectedStorages(World world, BlockPos pos) {
        return findAllDrawers(world, pos).stream()
                .map(world::getBlockEntity)
                .map(DrawerBlockEntity.class::cast)
                .filter(Objects::nonNull)
                .flatMap(drawer -> Arrays.stream(drawer.storages))
                .sorted()
                .toList();
    }
    
    public static List<BlockPos> findAllDrawers(World world, BlockPos pos) {
        return NetworkHelper.findConnectedComponents(world, pos, (world1, pos1) -> world1.getBlockState(pos1).getBlock() instanceof DrawerBlock);
    }
}
