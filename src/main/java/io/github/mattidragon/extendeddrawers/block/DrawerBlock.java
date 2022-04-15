package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static io.github.mattidragon.extendeddrawers.util.DrawerInteractionStatusManager.getAndResetExtractionTimer;
import static io.github.mattidragon.extendeddrawers.util.DrawerInteractionStatusManager.getAndResetInsertStatus;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class DrawerBlock extends BaseBlock<DrawerBlockEntity> implements Lockable, NetworkComponent {
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
    
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DrawerBlockEntity(pos, state);
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        var internalPos = calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING));
        if (internalPos == null) return ActionResult.PASS;
        var slot = getSlot(internalPos);
        
        var drawer = getBlockEntity(world, pos);
        var playerStack = player.getStackInHand(hand);
        var insertStatus = getAndResetInsertStatus(player, pos, slot, ItemVariant.of(playerStack));
    
        try (var t = Transaction.openOuter()) {
            int inserted;
    
            var storage = drawer.storages[slot];
    
            if (insertStatus.isPresent()) {
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, itemVariant -> itemVariant.equals(insertStatus.get()), storage.getCapacity() - storage.amount, t);
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
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (world.isClient) return;
        
        var drawer = getBlockEntity(world, pos);
        if (!getAndResetExtractionTimer(player)) return; // Mojank moment
        
        var hit = getTarget(player, pos);
        if (hit.getType() == HitResult.Type.MISS) return;
    
        var internalPos = calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING));
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
    
    @Nullable
    private Vec2f calculateFaceLocation(BlockPos blockPos, Vec3d hitPos, Direction hitDirection, Direction blockDirection) {
        if (hitDirection != blockDirection) return null;
        var internalPos = hitPos.subtract(Vec3d.of(blockPos));
        
        return switch (blockDirection) {
            case NORTH -> new Vec2f((float) (1 - internalPos.x), (float) (1 - internalPos.y));
            case SOUTH -> new Vec2f((float) (internalPos.x), (float) (1 - internalPos.y));
            case EAST -> new Vec2f((float) (1 - internalPos.z), (float) (1 -internalPos.y));
            case WEST -> new Vec2f((float) (internalPos.z), (float) (1 - internalPos.y));
            default -> null;
        };
    }
    
    private BlockHitResult getTarget(PlayerEntity player, BlockPos target) {
        var from = player.getEyePos();
        var length = Vec3d.ofCenter(target).subtract(from).length() + 1; //Add a bit of extra length for consistency
        var looking = player.getRotationVector();
        var to = from.add(looking.multiply(length));
        return player.world.raycast(new RaycastContext(from, to, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
    }
    
    @Override
    public void toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return;
        var storage = getBlockEntity(world, pos).storages[getSlot(facePos)];
        storage.setLocked(!storage.locked);
    }
}
