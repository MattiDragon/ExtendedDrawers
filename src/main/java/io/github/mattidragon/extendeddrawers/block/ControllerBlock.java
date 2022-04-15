package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.entity.ControllerBlockEntity;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class ControllerBlock extends BaseBlock<ControllerBlockEntity> implements Lockable, NetworkComponent {
    public ControllerBlock(Settings settings) {
        super(settings);
    
        //noinspection UnstableApiUsage
        ItemStorage.SIDED.registerForBlocks((world, pos, state, entity, dir) -> new CombinedStorage<>(getConnectedStorages(world, pos)), this);
    }
    
    @Override
    protected BlockEntityType<ControllerBlockEntity> getType() {
        return ModBlocks.CONTROLLER_BLOCK_ENTITY;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var entity = getBlockEntity(world, pos);
        var insertAll = world.getTime() - entity.lastInsertTimestamp < 10; // TODO: add config
    
        try (var t = Transaction.openOuter()) {
            int inserted;
        
            var storage = ItemStorage.SIDED.find(world, pos, state, entity, Direction.UP);
            if (storage == null) throw new IllegalStateException("Controller doesn't have storage!");
        
            if (insertAll) {
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, itemVariant -> itemVariant.equals(entity.lastInsertType), Long.MAX_VALUE, t);
                entity.lastInsertTimestamp = -1;
            } else {
                var playerStack = player.getStackInHand(hand);
                if (playerStack.isEmpty()) return ActionResult.PASS;
            
                inserted = (int) storage.insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                entity.lastInsertTimestamp = world.getTime();
                entity.lastInsertType = ItemVariant.of(playerStack);
                playerStack.decrement(inserted);
            }
            if (inserted == 0) return ActionResult.PASS;
        
            t.commit();
            return ActionResult.SUCCESS;
        }
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
