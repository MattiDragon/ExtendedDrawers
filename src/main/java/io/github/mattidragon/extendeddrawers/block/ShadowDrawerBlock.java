package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.BaseBlock;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.util.DrawerRaycastUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static io.github.mattidragon.extendeddrawers.util.DrawerInteractionStatusManager.getAndResetExtractionTimer;
import static io.github.mattidragon.extendeddrawers.util.DrawerInteractionStatusManager.getAndResetInsertStatus;

@SuppressWarnings({"UnstableApiUsage", "deprecation"}) // transfer api and mojank block method deprecation
public class ShadowDrawerBlock extends BaseBlock<ShadowDrawerBlockEntity> {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    
    public ShadowDrawerBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }
    
    
    
    @Override
    protected BlockEntityType<ShadowDrawerBlockEntity> getType() {
        return ModBlocks.SHADOW_DRAWER_BLOCK_ENTITY;
    }
    
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hit.getSide() != state.get(FACING)) return ActionResult.PASS;
        
        var drawer = getBlockEntity(world, pos);
        var playerStack = player.getStackInHand(hand);
        
        if (player.isSneaking() || drawer.item.isBlank()) {
            drawer.item = ItemVariant.of(playerStack);
            drawer.markDirty();
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            return ActionResult.SUCCESS;
        }
        
        
        
        var insertStatus = getAndResetInsertStatus(player, pos, 0, ItemVariant.of(playerStack));
        
        try (var t = Transaction.openOuter()) {
            int inserted;
    
            var storage = ItemStorage.SIDED.find(world, pos, state, drawer, Direction.UP);
            if (storage == null) throw new IllegalStateException("Shadow drawer doesn't have storage!");
    
            if (insertStatus.isPresent()) {
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, itemVariant -> itemVariant.equals(insertStatus.get()), Long.MAX_VALUE, t);
            } else {
                if (!ItemVariant.of(playerStack).equals(drawer.item) || playerStack.isEmpty()) return ActionResult.PASS;
    
                inserted = (int) storage.insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                playerStack.decrement(inserted);
            }
            if (inserted == 0) return ActionResult.PASS;
            
            t.commit();
            return ActionResult.SUCCESS;
        }
    }
    
    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient) return;
        
        var drawer = getBlockEntity(world, pos);
        if (!getAndResetExtractionTimer(player)) return; // Mojank moment
        
        var hit = DrawerRaycastUtil.getTarget(player, pos);
        if (hit.getType() == HitResult.Type.MISS) return;
        
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING));
        if (internalPos == null) return;
    
        var storage = ItemStorage.SIDED.find(world, pos, state, drawer, Direction.UP);
        if (storage == null) throw new IllegalStateException("Shadow drawer doesn't have storage!");
        
        try (var t = Transaction.openOuter()) {
            // cache because it changes
            var extracted = (int) storage.extract(drawer.item, player.isSneaking() ? drawer.item.getItem().getMaxCount() : 1, t);
            if (extracted == 0) return;
            
            player.getInventory().insertStack(drawer.item.toStack(extracted));
            t.commit();
        }
    }
}
