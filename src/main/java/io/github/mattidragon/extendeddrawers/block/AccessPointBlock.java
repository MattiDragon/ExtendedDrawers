package io.github.mattidragon.extendeddrawers.block;

import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.block.base.NetworkBlock;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.network.NetworkRegistry;
import io.github.mattidragon.extendeddrawers.network.cache.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.network.node.AccessPointBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.storage.DrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static io.github.mattidragon.extendeddrawers.misc.DrawerInteractionStatusManager.getAndResetInsertStatus;

public class AccessPointBlock extends NetworkBlock implements DrawerInteractionHandler {
    public AccessPointBlock(Settings settings) {
        super(settings);
    
        ItemStorage.SIDED.registerForBlocks((world, pos, state, entity, dir) -> world instanceof ServerWorld serverWorld ? NetworkStorageCache.get(serverWorld, pos) : Storage.empty(), this);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld)
            return StorageUtil.calculateComparatorOutput(NetworkStorageCache.get(serverWorld, pos));
        return 0;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!player.canModifyBlocks()) return ActionResult.PASS;
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.CONSUME;

        var storage = NetworkStorageCache.get(serverWorld, pos);

        try (var t = Transaction.openOuter()) {
            int inserted;
    
            var playerStack = player.getMainHandStack();
            var isDoubleClick = getAndResetInsertStatus(player, pos, 0);
            
            if (isDoubleClick) {
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, itemVariant -> {
                    for (var view : storage) {
                        if (view.getResource().equals(itemVariant)) {
                            return true;
                        }
                    }

                    return false;
                }, Long.MAX_VALUE, t);
            } else {
                if (playerStack.isEmpty()) return ActionResult.PASS;
            
                inserted = (int) storage.insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                playerStack.decrement(inserted);
            }
            if (inserted == 0) return ActionResult.CONSUME;
        
            t.commit();
            return ActionResult.SUCCESS;
        }
    }
    
    @Override
    public ActionResult toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;
        var storages = NetworkStorageCache.get(serverWorld, pos).parts;
        var newState = storages.stream()
                .map(DrawerStorage::isLocked)
                .mapToInt(value -> value ? 1 : -1)
                .sum() <= 0;
        storages.forEach(storage -> storage.setLocked(newState));
    
        return storages.isEmpty() ? ActionResult.PASS : ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleVoid(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;
        var storages = NetworkStorageCache.get(serverWorld, pos).parts;
        var newState = storages.stream()
                .map(DrawerStorage::isVoiding)
                .mapToInt(value -> value ? 1 : -1)
                .sum() <= 0;
        storages.forEach(storage -> storage.setVoiding(newState));

        return storages.isEmpty() ? ActionResult.PASS : ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleDuping(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;
        var storages = NetworkStorageCache.get(serverWorld, pos).parts;
        var newState = storages.stream()
                .map(DrawerStorage::isDuping)
                .mapToInt(value -> value ? 1 : -1)
                .sum() <= 0;
        storages.forEach(storage -> storage.setDuping(newState));

        return storages.isEmpty() ? ActionResult.PASS : ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleHide(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.PASS;
        var storages = NetworkStorageCache.get(serverWorld, pos).parts;
        var shadowDrawers = NetworkRegistry.UNIVERSE.getGraphWorld(serverWorld)
                .getLoadedGraphsAt(pos)
                .flatMap(BlockGraph::getNodes)
                .map(NodeHolder::getBlockPos)
                .map(serverWorld::getBlockEntity)
                .filter(ShadowDrawerBlockEntity.class::isInstance)
                .map(ShadowDrawerBlockEntity.class::cast)
                .toList();

        var sum = storages.stream()
                .map(DrawerStorage::isHidden)
                .mapToInt(value -> value ? 1 : -1)
                .sum();
        sum += shadowDrawers.stream()
                .map(ShadowDrawerBlockEntity::isHidden)
                .mapToInt(value -> value ? 1 : -1)
                .sum();


        var newState = sum <= 0;
        storages.forEach(storage -> storage.setHidden(newState));
        shadowDrawers.forEach(drawer -> drawer.setHidden(newState));

        return storages.size() + shadowDrawers.size() == 0 ? ActionResult.PASS : ActionResult.SUCCESS;
    }

    @Override
    public DrawerNetworkBlockNode getNode() {
        return AccessPointBlockNode.INSTANCE;
    }
}
