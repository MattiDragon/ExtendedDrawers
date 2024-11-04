package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.block.base.NetworkBlockWithEntity;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.misc.DrawerRaycastUtil;
import io.github.mattidragon.extendeddrawers.network.cache.NetworkStorageCache;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.ShadowDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.misc.DrawerInteractionStatusManager.getAndResetInsertStatus;

public class ShadowDrawerBlock extends NetworkBlockWithEntity<ShadowDrawerBlockEntity> implements CreativeBreakBlocker, DrawerInteractionHandler {
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    
    public ShadowDrawerBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(FACE, BlockFace.WALL));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return;
        drawer.recalculateContents();
    }

    private static Storage<ItemVariant> createStorage(ServerWorld world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof ShadowDrawerBlockEntity shadowDrawer)) throw new IllegalStateException();

        return shadowDrawer.new ShadowDrawerStorage(NetworkStorageCache.get(world, pos));
    }

    @Override
    protected BlockEntityType<ShadowDrawerBlockEntity> getType() {
        return ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY;
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!isFront(state, hit.getSide()) || !player.canModifyBlocks()) return ActionResult.PASS;
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.CONSUME;

        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return ActionResult.PASS;
        var playerStack = player.getMainHandStack();
        
        if (player.isSneaking() || drawer.item.isBlank()) {
            drawer.item = ItemVariant.of(playerStack);
            drawer.markDirty();
            drawer.recalculateContents();
            return ActionResult.SUCCESS;
        }
        
        var isDoubleClick = getAndResetInsertStatus(player, pos, 0);
        
        try (var t = Transaction.openOuter()) {
            int inserted;
    
            var storage = createStorage(serverWorld, pos);
    
            if (isDoubleClick) {
                if (drawer.item.isBlank()) return ActionResult.PASS;
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, itemVariant -> true, Long.MAX_VALUE, t);
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
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!player.canModifyBlocks()) return;
        if (!(world instanceof ServerWorld serverWorld)) return;

        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return;
        
        var hit = DrawerRaycastUtil.getTarget(player, pos);
        if (hit.getType() == HitResult.Type.MISS) return;
        
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING), state.get(FACE));
        if (internalPos == null) return;
    
        var storage = createStorage(serverWorld, pos);
        
        try (var t = Transaction.openOuter()) {
            var extracted = (int) storage.extract(drawer.item, player.isSneaking() ? drawer.item.getItem().getMaxCount() : 1, t);
            if (extracted == 0) return;
            
            player.getInventory().offerOrDrop(drawer.item.toStack(extracted));
            t.commit();
        }
    }
    
    @Override
    public boolean isFront(BlockState state, Direction direction) {
        return switch (state.get(FACE)) {
            case FLOOR -> direction == Direction.UP;
            case CEILING -> direction == Direction.DOWN;
            case WALL -> direction == state.get(FACING);
        };
    }

    @Override
    public DrawerNetworkBlockNode getNode() {
        return ShadowDrawerBlockNode.INSTANCE;
    }

    @Override
    public ActionResult toggleHide(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        if (side != state.get(FACING)) return ActionResult.PASS;
        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return ActionResult.PASS;
        drawer.setHidden(!drawer.isHidden());
        return ActionResult.SUCCESS;
    }
}
