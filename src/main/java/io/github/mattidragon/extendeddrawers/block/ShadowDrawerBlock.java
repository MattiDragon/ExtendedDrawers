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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.misc.DrawerInteractionStatusManager.getAndResetInsertStatus;

public class ShadowDrawerBlock extends NetworkBlockWithEntity<ShadowDrawerBlockEntity> implements CreativeBreakBlocker, DrawerInteractionHandler {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;
    
    public ShadowDrawerBlock(Properties settings) {
        super(settings);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);
        var drawer = getBlockEntity(level, pos);
        if (drawer == null) return;
        drawer.recalculateContents();
    }

    private static Storage<ItemVariant> createStorage(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ShadowDrawerBlockEntity shadowDrawer)) throw new IllegalStateException();

        return shadowDrawer.new ShadowDrawerStorage(NetworkStorageCache.get(level, pos));
    }

    @Override
    protected BlockEntityType<ShadowDrawerBlockEntity> getType() {
        return ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var face = switch (context.getNearestLookingDirection().getOpposite()) {
            case DOWN -> AttachFace.CEILING;
            case UP -> AttachFace.FLOOR;
            default -> AttachFace.WALL;
        };

        return this.defaultBlockState()
                .setValue(FACE, face)
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
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
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!isFront(state, hitResult.getDirection()) || !player.mayBuild()) return InteractionResult.PASS;
        if (!(level instanceof ServerLevel serverLevel)) return InteractionResult.CONSUME;

        var drawer = getBlockEntity(level, pos);
        if (drawer == null) return InteractionResult.PASS;
        var playerStack = player.getMainHandItem();
        
        if (player.isShiftKeyDown() || drawer.item.isBlank()) {
            drawer.item = ItemVariant.of(playerStack);
            drawer.setChanged();
            drawer.recalculateContents();
            return InteractionResult.SUCCESS;
        }
        
        var isDoubleClick = getAndResetInsertStatus(player, pos, 0);
        
        try (var t = Transaction.openOuter()) {
            int inserted;
    
            var storage = createStorage(serverLevel, pos);
    
            if (isDoubleClick) {
                if (drawer.item.isBlank()) return InteractionResult.PASS;
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, _ -> true, Long.MAX_VALUE, t);
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
    
    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!player.mayBuild()) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        var drawer = getBlockEntity(level, pos);
        if (drawer == null) return;
        
        var hit = DrawerRaycastUtil.getTarget(player, pos);
        if (hit.getType() == HitResult.Type.MISS) return;
        
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getLocation(), hit.getDirection(), state.getValue(FACING), state.getValue(FACE));
        if (internalPos == null) return;
    
        var storage = createStorage(serverLevel, pos);
        
        try (var t = Transaction.openOuter()) {
            var extracted = (int) storage.extract(drawer.item, player.isShiftKeyDown() ? drawer.item.toStack().getMaxStackSize() : 1, t);
            if (extracted == 0) return;
            
            player.getInventory().placeItemBackInInventory(drawer.item.toStack(extracted));
            t.commit();
        }
    }
    
    @Override
    public boolean isFront(BlockState state, Direction direction) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> direction == Direction.UP;
            case CEILING -> direction == Direction.DOWN;
            case WALL -> direction == state.getValue(FACING);
        };
    }

    @Override
    public DrawerNetworkBlockNode getNode() {
        return ShadowDrawerBlockNode.INSTANCE;
    }

    @Override
    public InteractionResult toggleHide(BlockState state, Level level, BlockPos pos, Vec3 hitPos, Direction side) {
        if (side != state.getValue(FACING)) return InteractionResult.PASS;
        var drawer = getBlockEntity(level, pos);
        if (drawer == null) return InteractionResult.PASS;
        drawer.setHidden(!drawer.isHidden());
        return InteractionResult.SUCCESS;
    }
}
