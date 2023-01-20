package io.github.mattidragon.extendeddrawers.block;

import com.kneelawk.graphlib.graph.BlockNode;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.block.base.NetworkBlockWithEntity;
import io.github.mattidragon.extendeddrawers.block.entity.ShadowDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.misc.DrawerRaycastUtil;
import io.github.mattidragon.extendeddrawers.network.NetworkStorageCache;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static io.github.mattidragon.extendeddrawers.misc.DrawerInteractionStatusManager.getAndResetInsertStatus;

@SuppressWarnings({"UnstableApiUsage", "deprecation"}) // transfer api and mojank block method deprecation
public class ShadowDrawerBlock extends NetworkBlockWithEntity<ShadowDrawerBlockEntity> implements CreativeBreakBlocker, DrawerInteractionHandler {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    
    public ShadowDrawerBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }
    
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (world instanceof ServerWorld serverWorld)
            return StorageUtil.calculateComparatorOutput(createStorage(serverWorld, pos));
        return 0;
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        getBlockEntity(world, pos).recalculateContents();
    }

    private static Storage<ItemVariant> createStorage(ServerWorld world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof ShadowDrawerBlockEntity shadowDrawer)) throw new IllegalStateException();

        return shadowDrawer.new ShadowDrawerStorage(NetworkStorageCache.get(world, pos));
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
        if (hit.getSide() != state.get(FACING) || !player.canModifyBlocks()) return ActionResult.PASS;
        if (!(world instanceof ServerWorld serverWorld)) return ActionResult.CONSUME_PARTIAL;

        var drawer = getBlockEntity(world, pos);
        var playerStack = player.getStackInHand(hand);
        
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
            if (inserted == 0) return ActionResult.CONSUME_PARTIAL;
            
            t.commit();
            return ActionResult.SUCCESS;
        }
    }
    
    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!player.canModifyBlocks()) return;
        if (!(world instanceof ServerWorld serverWorld)) return;

        var drawer = getBlockEntity(world, pos);
        
        var hit = DrawerRaycastUtil.getTarget(player, pos);
        if (hit.getType() == HitResult.Type.MISS) return;
        
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING));
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
    public boolean shouldBlock(World world, BlockPos pos, Direction direction) {
        return world.getBlockState(pos).get(FACING) == direction;
    }

    @Override
    public Collection<BlockNode> createNodes() {
        return List.of(new ShadowDrawerBlockNode());
    }

    @Override
    public ActionResult toggleHide(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        if (side != state.get(FACING)) return ActionResult.PASS;
        var drawer = getBlockEntity(world, pos);
        drawer.setHidden(!drawer.isHidden());
        return ActionResult.SUCCESS;
    }
}
