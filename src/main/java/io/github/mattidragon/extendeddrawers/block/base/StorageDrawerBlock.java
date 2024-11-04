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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class StorageDrawerBlock<T extends StorageDrawerBlockEntity> extends NetworkBlockWithEntity<T> implements DrawerInteractionHandler, CreativeBreakBlocker {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;

    protected StorageDrawerBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(FACE, BlockFace.WALL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        var face = switch (ctx.getPlayerLookDirection().getOpposite()) {
            case DOWN -> BlockFace.CEILING;
            case UP -> BlockFace.FLOOR;
            default -> BlockFace.WALL;
        };

        return this.getDefaultState()
                .with(FACE, face)
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        var blockEntity = getBlockEntity(world, pos);
        if (blockEntity != null && ExtendedDrawers.CONFIG.get().misc().dropDrawersInCreative() && !world.isClient && player.isCreative() && !blockEntity.isEmpty()) {
            getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, player.getStackInHand(Hand.MAIN_HAND))
                    .forEach(stack -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!player.canModifyBlocks()) return;

        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return;

        // We don't have sub-block position or a hit result, so we need to raycast ourselves
        var hit = DrawerRaycastUtil.getTarget(player, pos);
        if (hit.getType() == HitResult.Type.MISS) return;
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING), state.get(FACE));
        if (internalPos == null) return;

        var storage = getSlot(drawer, getSlotIndex(drawer, internalPos));
        if (storage.isResourceBlank()) return;

        try (var t = Transaction.openOuter()) {
            var item = storage.getResource(); // cache because it changes
            var extracted = (int) storage.extract(item, player.isSneaking() ? item.getItem().getMaxCount() : 1, t);
            if (extracted == 0) return;

            player.getInventory().offerOrDrop(item.toStack(extracted));

            t.commit();
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!isFront(state, hit.getSide()) || !player.canModifyBlocks())
            return ActionResult.PASS;
        if (!(world instanceof ServerWorld)) return ActionResult.CONSUME;

        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING), state.get(FACE));
        if (internalPos == null) return ActionResult.PASS;

        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return ActionResult.PASS;
        var slot = getSlotIndex(drawer, internalPos);
        var storage = getSlot(drawer, slot);

        ModifierAccess modifiers = getModifierAccess(drawer, internalPos);
        var playerStack = player.getMainHandStack();

        // Upgrade & limiter removal
        if (playerStack.isEmpty() && player.isSneaking()) {
            // remove limiter first, if that fails, remove upgrade
            var changeResult = modifiers.changeLimiter(ItemVariant.blank(), world, pos, hit.getSide(), player)
                               || modifiers.changeUpgrade(ItemVariant.blank(), world, pos, hit.getSide(), player);
            return changeResult ? ActionResult.SUCCESS : ActionResult.FAIL;
        }

        var isDoubleClick = DrawerInteractionStatusManager.getAndResetInsertStatus(player, pos, slot);

        try (var t = Transaction.openOuter()) {
            int inserted;

            modifiers.overrideLock(t);
            if (isDoubleClick) {
                if (storage.isResourceBlank()) return ActionResult.PASS;
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), (SingleSlotStorage<ItemVariant>) storage, itemVariant -> true, Long.MAX_VALUE, t);
            } else {
                if (playerStack.isEmpty()) return ActionResult.PASS;

                inserted = (int) ((SingleSlotStorage<ItemVariant>) storage).insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                playerStack.decrement(inserted);
            }
            if (inserted == 0) return ActionResult.CONSUME;

            t.commit();
            return ActionResult.CONSUME;
        }
    }

    public abstract int getSlotIndex(T drawer, Vec2f facePos);

    public abstract StorageView<ItemVariant> getSlot(T drawer, int slot);

    @Override
    public abstract int getComparatorOutput(BlockState state, World world, BlockPos pos);

    protected abstract ModifierAccess getModifierAccess(T drawer, Vec2f facePos);

    protected @Nullable ModifierAccess tryGetModifierAccess(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(StorageDrawerBlock.FACING), state.get(FACE));
        if (facePos == null) return null;
        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return null;
        return getModifierAccess(drawer, facePos);
    }

    @Override
    public ActionResult toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return ActionResult.PASS;
        access.setLocked(!access.isLocked());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleVoid(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return ActionResult.PASS;
        access.setVoiding(!access.isVoiding());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleHide(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return ActionResult.PASS;
        access.setHidden(!access.isHidden());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleDuping(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return ActionResult.PASS;
        access.setDuping(!access.isDuping());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult changeUpgrade(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, PlayerEntity player, ItemStack stack) {
        if (world.isClient) return ActionResult.SUCCESS;

        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return ActionResult.PASS;

        if (!(stack.getItem() instanceof UpgradeItem)) {
            ExtendedDrawers.LOGGER.warn("Expected drawer upgrade to be UpgradeItem but found {} instead", stack.getItem().getClass().getSimpleName());
            return ActionResult.FAIL;
        }

        var changed = access.changeUpgrade(ItemVariant.of(stack), world, pos, side, player);
        if (changed)
            stack.decrement(1);

        return changed ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

    @Override
    public ActionResult changeLimiter(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, PlayerEntity player, ItemStack stack) {
        if (world.isClient) return ActionResult.SUCCESS;

        var access = tryGetModifierAccess(state, world, pos, hitPos, side);
        if (access == null) return ActionResult.PASS;

        if (!stack.isOf(ModItems.LIMITER)) {
            ExtendedDrawers.LOGGER.warn("Expected limiter to be limiter but found {} instead", stack);
            return ActionResult.FAIL;
        }

        var changed = access.changeLimiter(ItemVariant.of(stack), world, pos, side, player);
        if (changed) {
            stack.decrement(1);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    public static Direction getFront(BlockState state) {
        return switch (state.get(FACE)) {
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
            case WALL -> state.get(FACING);
        };
    }

    @Override
    public boolean isFront(BlockState state, Direction direction) {
        return switch (state.get(FACE)) {
            case FLOOR -> direction == Direction.UP;
            case CEILING -> direction == Direction.DOWN;
            case WALL -> direction == state.get(FACING);
        };
    }
}
