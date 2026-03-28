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
import io.github.mattidragon.extendeddrawers.storage.ModifierDrawerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static io.github.mattidragon.extendeddrawers.misc.DrawerInteractionStatusManager.getAndResetInsertStatus;

public class AccessPointBlock extends NetworkBlock implements DrawerInteractionHandler {
    public AccessPointBlock(Properties settings) {
        super(settings);

        ItemStorage.SIDED.registerForBlocks((level, pos, _, _, _) ->
                        level instanceof ServerLevel serverWorld
                                ? NetworkStorageCache.get(serverWorld, pos)
                                : Storage.empty(),
                this);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level instanceof ServerLevel serverLevel)
            return StorageUtil.getRedstoneSignal(NetworkStorageCache.get(serverLevel, pos));
        return 0;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.mayBuild()) return InteractionResult.PASS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.CONSUME;

        var storage = NetworkStorageCache.get(serverLevel, pos);

        try (var t = Transaction.openOuter()) {
            int inserted;

            var playerStack = player.getMainHandItem();
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
                if (playerStack.isEmpty()) return InteractionResult.PASS;

                inserted = (int) storage.insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                playerStack.shrink(inserted);
            }
            if (inserted == 0) return InteractionResult.CONSUME;

            t.commit();
            return InteractionResult.SUCCESS;
        }
    }

    private static List<ModifierDrawerStorage> getModifierStorages(BlockPos pos, ServerLevel level) {
        return NetworkStorageCache.get(level, pos).parts
                .stream()
                .filter(ModifierDrawerStorage.class::isInstance)
                .map(ModifierDrawerStorage.class::cast)
                .toList();
    }

    @Override
    public InteractionResult toggleLock(BlockState state, Level level, BlockPos pos, Vec3 hitPos, Direction side) {
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
        var storages = getModifierStorages(pos, serverLevel);
        var newState = storages.stream()
                .map(DrawerStorage::isLocked)
                .mapToInt(value -> value ? 1 : -1)
                .sum() <= 0;
        storages.forEach(storage -> storage.setLocked(newState));

        return storages.isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult toggleVoid(BlockState state, Level level, BlockPos pos, Vec3 hitPos, Direction side) {
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
        var storages = getModifierStorages(pos, serverLevel);
        var newState = storages.stream()
                .map(DrawerStorage::isVoiding)
                .mapToInt(value -> value ? 1 : -1)
                .sum() <= 0;
        storages.forEach(storage -> storage.setVoiding(newState));

        return storages.isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult toggleDuping(BlockState state, Level level, BlockPos pos, Vec3 hitPos, Direction side) {
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
        var storages = getModifierStorages(pos, serverLevel);
        var newState = storages.stream()
                .map(DrawerStorage::isDuping)
                .mapToInt(value -> value ? 1 : -1)
                .sum() <= 0;
        storages.forEach(storage -> storage.setDuping(newState));

        return storages.isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult toggleHide(BlockState state, Level level, BlockPos pos, Vec3 hitPos, Direction side) {
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.PASS;
        var storages = getModifierStorages(pos, serverLevel);
        var shadowDrawers = NetworkRegistry.UNIVERSE.getGraphWorld(serverLevel)
                .getLoadedGraphsAt(pos)
                .flatMap(BlockGraph::getNodes)
                .map(NodeHolder::getBlockPos)
                .map(serverLevel::getBlockEntity)
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

        return storages.size() + shadowDrawers.size() == 0 ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }

    @Override
    public DrawerNetworkBlockNode getNode() {
        return AccessPointBlockNode.INSTANCE;
    }
}
