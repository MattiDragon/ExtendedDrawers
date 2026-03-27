package io.github.mattidragon.extendeddrawers.block.base;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.entity.StorageDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.DrawerInteractionStatusManager;
import io.github.mattidragon.extendeddrawers.misc.DrawerRaycastUtil;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.storage.ModifierAccess;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class StorageDrawerBlock<T extends StorageDrawerBlockEntity> extends NetworkBlockWithEntity<T> implements DrawerInteractionHandler, CreativeBreakBlocker {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;

    protected StorageDrawerBlock(Properties settings) {
        super(settings);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var face = switch (ctx.getNearestLookingDirection().getOpposite()) {
            case DOWN -> AttachFace.CEILING;
            case UP -> AttachFace.FLOOR;
            default -> AttachFace.WALL;
        };

        return this.defaultBlockState()
                .setValue(FACE, face)
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        var blockEntity = getBlockEntity(world, pos);
        if (blockEntity != null && ExtendedDrawers.CONFIG.get().misc().dropDrawersInCreative() && !world.isClientSide() && player.isCreative() && !blockEntity.isEmpty()) {
            getDrops(state, (ServerLevel) world, pos, blockEntity, player, player.getItemInHand(InteractionHand.MAIN_HAND))
                    .forEach(stack -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (!player.mayBuild()) return;

        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return;

        // We don't have sub-block position or a hit result, so we need to raycast ourselves
        var hit = DrawerRaycastUtil.getTarget(player, pos);
        if (hit.getType() == HitResult.Type.MISS) return;
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getLocation(), hit.getDirection(), state.getValue(FACING), state.getValue(FACE));
        if (internalPos == null) return;

        var storage = getSlot(drawer, getSlotIndex(drawer, internalPos));
        if (storage.isResourceBlank()) return;

        try (var t = Transaction.openOuter()) {
            var item = storage.getResource(); // cache because it changes
            var extracted = (int) storage.extract(item, player.isShiftKeyDown() ? item.getItem().getDefaultMaxStackSize() : 1, t);
            if (extracted == 0) return;

            player.getInventory().placeItemBackInInventory(item.toStack(extracted));

            t.commit();
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!isFront(state, hit.getDirection()) || !player.mayBuild())
            return InteractionResult.PASS;
        if (!(world instanceof ServerLevel)) return InteractionResult.CONSUME;

        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getLocation(), hit.getDirection(), state.getValue(FACING), state.getValue(FACE));
        if (internalPos == null) return InteractionResult.PASS;

        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return InteractionResult.PASS;
        var slot = getSlotIndex(drawer, internalPos);
        var storage = getSlot(drawer, slot);

        ModifierAccess modifiers = getModifierAccess(drawer, internalPos);
        var playerStack = player.getMainHandItem();

        // Upgrade & limiter removal
        if (playerStack.isEmpty() && player.isShiftKeyDown()) {
            // remove limiter first, if that fails, remove upgrade
            var changeResult = modifiers.changeLimiter(ItemVariant.blank(), world, pos, hit.getDirection(), player)
                               || modifiers.changeUpgrade(ItemVariant.blank(), world, pos, hit.getDirection(), player);
            return changeResult ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }

        var isDoubleClick = DrawerInteractionStatusManager.getAndResetInsertStatus(player, pos, slot);

        try (var t = Transaction.openOuter()) {
            int inserted;

            modifiers.overrideLock(t);
            if (isDoubleClick) {
                if (storage.isResourceBlank()) return InteractionResult.PASS;
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), (SingleSlotStorage<ItemVariant>) storage, itemVariant -> true, Long.MAX_VALUE, t);
            } else {
                if (playerStack.isEmpty()) return InteractionResult.PASS;

                inserted = (int) ((SingleSlotStorage<ItemVariant>) storage).insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                playerStack.shrink(inserted);
            }
            if (inserted == 0) return InteractionResult.CONSUME;

            t.commit();
            return InteractionResult.CONSUME;
        }
    }

    public abstract int getSlotIndex(T drawer, Vec2 facePos);

    public abstract StorageView<ItemVariant> getSlot(T drawer, int slot);

    @Override
    public abstract int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos, Direction direction);

    protected abstract ModifierAccess getModifierAccess(T drawer, Vec2 facePos);

    protected @Nullable ModifierAccess tryGetModifierAccess(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.getValue(StorageDrawerBlock.FACING), state.getValue(FACE));
        if (facePos == null) return null;
        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return null;
        return getModifierAccess(drawer, facePos);
    }

    @Override
    public InteractionResult toggleLock(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return InteractionResult.PASS;
        access.setLocked(!access.isLocked());
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult toggleVoid(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return InteractionResult.PASS;
        access.setVoiding(!access.isVoiding());
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult toggleHide(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return InteractionResult.PASS;
        access.setHidden(!access.isHidden());
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult toggleDuping(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side) {
        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return InteractionResult.PASS;
        access.setDuping(!access.isDuping());
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult changeUpgrade(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side, @Nullable Player player, ItemStack stack) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;

        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return InteractionResult.PASS;

        if (!(stack.getItem() instanceof UpgradeItem)) {
            ExtendedDrawers.LOGGER.warn("Expected drawer upgrade to be UpgradeItem but found {} instead", stack.getItem().getClass().getSimpleName());
            return InteractionResult.FAIL;
        }

        var changed = access.changeUpgrade(ItemVariant.of(stack), world, pos, side, player);
        if (changed)
            stack.shrink(1);

        return changed ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    public InteractionResult changeLimiter(BlockState state, Level world, BlockPos pos, Vec3 hitPos, Direction side, @Nullable Player player, ItemStack stack) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;

        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return InteractionResult.PASS;

        if (!stack.is(ModItems.LIMITER)) {
            ExtendedDrawers.LOGGER.warn("Expected limiter to be limiter but found {} instead", stack);
            return InteractionResult.FAIL;
        }

        var changed = access.changeLimiter(ItemVariant.of(stack), world, pos, side, player);
        if (changed) {
            stack.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    public static Direction getFront(BlockState state) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
            case WALL -> state.getValue(FACING);
        };
    }

    @Override
    public boolean isFront(BlockState state, Direction direction) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> direction == Direction.UP;
            case CEILING -> direction == Direction.DOWN;
            case WALL -> direction == state.getValue(FACING);
        };
    }
}
