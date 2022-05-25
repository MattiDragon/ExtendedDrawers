package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.BaseBlock;
import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.util.DrawerInteractionStatusManager;
import io.github.mattidragon.extendeddrawers.util.DrawerRaycastUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
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

import java.util.List;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

@SuppressWarnings({"UnstableApiUsage", "deprecation"}) // transfer api and mojank block method deprecation
public class DrawerBlock extends BaseBlock<DrawerBlockEntity> implements DrawerInteractionHandler {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    
    public final int slots;
    
    public DrawerBlock(Settings settings, int slots) {
        super(settings);
        this.slots = slots;
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }
    
    @Override
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
    protected BlockEntityType<DrawerBlockEntity> getType() {
        return ModBlocks.DRAWER_BLOCK_ENTITY;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.canModifyBlocks() || hand == Hand.OFF_HAND) return ActionResult.PASS;
        
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING));
        if (internalPos == null) return ActionResult.PASS;
        var slot = getSlot(internalPos);
        
        var drawer = getBlockEntity(world, pos);
        var playerStack = player.getStackInHand(hand);
        
        // Upgrade removal
        if (playerStack.isEmpty() && player.isSneaking()) {
            player.getInventory().offerOrDrop(new ItemStack(drawer.storages[slot].upgrade));
            drawer.storages[slot].upgrade = null;
            return ActionResult.SUCCESS;
        }
        
        var isDoubleClick = DrawerInteractionStatusManager.getAndResetInsertStatus(player, pos, slot);
    
        try (var t = Transaction.openOuter()) {
            int inserted;
            var storage = drawer.storages[slot];
    
            if (isDoubleClick) {
                if (storage.isResourceBlank()) return ActionResult.PASS;
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, itemVariant -> true, Long.MAX_VALUE, t);
            } else {
                if (playerStack.isEmpty()) return ActionResult.PASS;
                
                inserted = (int) storage.insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                playerStack.decrement(inserted);
            }
            if (inserted == 0) return ActionResult.PASS;
            
            t.commit();
            return ActionResult.CONSUME;
        }
    }
    
    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!player.canModifyBlocks()) return;
        
        var drawer = getBlockEntity(world, pos);
        
        // We don't have sub-block position or a hit result, so we need to raycast ourselves
        var hit = DrawerRaycastUtil.getTarget(player, pos);
        if (hit.getType() == HitResult.Type.MISS) return;
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING));
        if (internalPos == null) return;
    
        var slot = getSlot(internalPos);
        var storage = drawer.storages[slot];
        if (storage.isResourceBlank()) return;
        
        try (var t = Transaction.openOuter()) {
            var item = storage.item; // cache because it changes
            var extracted = (int) storage.extract(item, player.isSneaking() ? item.getItem().getMaxCount() : 1, t);
            if (extracted == 0) return;
    
            player.getInventory().insertStack(item.toStack(extracted));
            
            t.commit();
        }
    }
    
    private int getSlot(Vec2f facePos) {
        return switch (slots) {
            case 1 -> 0;
            case 2 -> facePos.x < 0.5f ? 0 : 1;
            case 4 -> facePos.y < 0.5f ? facePos.x < 0.5f ? 0 : 1 : facePos.x < 0.5f ? 2 : 3;
            default -> throw new IllegalStateException("unexpected drawer slot count");
        };
    }
    
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        if (builder.get(LootContextParameters.BLOCK_ENTITY) instanceof DrawerBlockEntity drawer) {
            builder = builder.putDrop(id("drawer"), (context, consumer) -> {
                for (var slot : drawer.storages) {
                    var amount = slot.amount;
                    var item = slot.item;
                    while (amount != 0) {
                        var count = Math.min((int) amount, item.getItem().getMaxCount());
                        consumer.accept(item.toStack(count));
                        amount -= count;
                    }
                }
            });
        }
        return super.getDroppedStacks(state, builder);
    }
    
    @Override
    public ActionResult toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storages[getSlot(facePos)];
        storage.setLocked(!storage.locked);
        return ActionResult.SUCCESS;
    }
    
    @Override
    public ActionResult upgrade(UpgradeItem upgrade, BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, PlayerEntity player, ItemStack stack) {
        if (world.isClient) return ActionResult.SUCCESS;
       
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storages[getSlot(facePos)];
        
        stack.decrement(1);
    
        // give back old one
        offerOrDrop(world, pos, side, player, new ItemStack(storage.upgrade));
    
        storage.upgrade = upgrade;
        
        // drop items that don't fit
        if (storage.amount > storage.getCapacity()) {
            var amount = storage.amount - storage.getCapacity();
            while (amount > 0) {
                int dropped = (int) Math.min(storage.item.getItem().getMaxCount(), amount);
                offerOrDrop(world, pos, side, player, storage.item.toStack(dropped));
                amount -= dropped;
                storage.amount -= dropped;
            }
        }
        
        storage.update();
        return ActionResult.SUCCESS;
    }
    
    private void offerOrDrop(World world, BlockPos pos, Direction side, @Nullable PlayerEntity player, ItemStack stack) {
        if (player == null)
            world.spawnEntity(new ItemEntity(world, pos.getX() + side.getOffsetX(), pos.getY(), pos.getZ() + side.getOffsetZ(), stack));
        else
            player.getInventory().offerOrDrop(stack);
    }
}
