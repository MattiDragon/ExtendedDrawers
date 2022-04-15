package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class ControllerBlock extends Block implements Lockable, NetworkComponent {
    public ControllerBlock(Settings settings) {
        super(settings);
    }
    
    @Override
    public void toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var storages = getConnectedStorages(world, pos);
        var stateSum = storages.stream()
                .map(storage -> storage.locked)
                .reduce(0, (count, value) -> count + (value ? 1 : -1), Integer::sum);
        var currentState = stateSum > 0;
        storages.forEach(storage -> storage.setLocked(!currentState));
    }
    
    public static List<DrawerBlockEntity.DrawerSlot> getConnectedStorages(World world, BlockPos pos) {
        return findAllDrawers(world, pos).stream()
                .map(world::getBlockEntity)
                .map(DrawerBlockEntity.class::cast)
                .filter(Objects::nonNull)
                .flatMap(drawer -> Arrays.stream(drawer.storages))
                .sorted(Comparator.comparingInt(storage -> storage.locked ? 1 : 0))
                .toList();
    }
    
    private static List<BlockPos> findAllDrawers(World world, BlockPos pos) {
        return NetworkComponent.findConnectedComponents(world, pos, (world1, pos1) -> world1.getBlockState(pos1).getBlock() instanceof DrawerBlock);
    }
}
