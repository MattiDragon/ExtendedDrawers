package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.util.NetworkHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static io.github.mattidragon.extendeddrawers.util.DrawerInteractionStatusManager.getAndResetInsertStatus;

@SuppressWarnings({"UnstableApiUsage", "deprecation"}) // transfer api and mojank block method deprecation
public class ControllerBlock extends Block implements DrawerInteractionHandler {
    public ControllerBlock(Settings settings) {
        super(settings);
    
        ItemStorage.SIDED.registerForBlocks((world, pos, state, entity, dir) -> new CombinedStorage<>(NetworkHelper.getConnectedStorages(world, pos)), this);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        try (var t = Transaction.openOuter()) {
            int inserted;
        
            var storage = ItemStorage.SIDED.find(world, pos, state, null, Direction.UP);
            if (storage == null) throw new IllegalStateException("Controller doesn't have storage!");
    
            var playerStack = player.getStackInHand(hand);
            var insertStatus = getAndResetInsertStatus(player, pos, 0, ItemVariant.of(playerStack));
            
            if (insertStatus.isPresent()) {
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, itemVariant -> itemVariant.equals(insertStatus.get()), Long.MAX_VALUE, t);
            } else {
                if (playerStack.isEmpty()) return ActionResult.PASS;
            
                inserted = (int) storage.insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                playerStack.decrement(inserted);
            }
            if (inserted == 0) return ActionResult.PASS;
        
            t.commit();
            return ActionResult.SUCCESS;
        }
    }
    
    @Override
    public ActionResult toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var storages = NetworkHelper.getConnectedStorages(world, pos);
        var stateSum = storages.stream()
                .map(storage -> storage.locked)
                .reduce(0, (count, value) -> count + (value ? 1 : -1), Integer::sum);
        var currentState = stateSum > 0;
        storages.forEach(storage -> storage.setLocked(!currentState));
    
        return storages.size() == 0 ? ActionResult.PASS : ActionResult.SUCCESS;
    }
}
