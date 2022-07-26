package io.github.mattidragon.extendeddrawers.block;

import com.kneelawk.graphlib.graph.BlockNode;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.block.base.NetworkBlockWithEntity;
import io.github.mattidragon.extendeddrawers.block.base.NetworkComponent;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.DrawerInteractionStatusManager;
import io.github.mattidragon.extendeddrawers.misc.DrawerRaycastUtil;
import io.github.mattidragon.extendeddrawers.network.node.DrawerBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

@SuppressWarnings({"UnstableApiUsage", "deprecation"}) // transfer api and mojank block method deprecation
public class DrawerBlock extends NetworkBlockWithEntity<DrawerBlockEntity> implements DrawerInteractionHandler, CreativeBreakBlocker, NetworkComponent {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public final int slots;

    public DrawerBlock(Settings settings, int slots) {
        super(settings);
        this.slots = slots;
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        var nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt == null) return;

        var list = nbt.getList("items", NbtElement.COMPOUND_TYPE).stream()
                .map(NbtCompound.class::cast)
                .map(slot -> new ResourceAmount<>(ItemVariant.fromNbt(slot.getCompound("item")), slot.getLong("amount")))
                .filter(resource -> !resource.resource().isBlank())
                .toList();
        if (list.isEmpty()) return;

        tooltip.add(Text.translatable("tooltip.extended_drawers.drawer_contents").formatted(Formatting.GRAY));
        for (var slot : list) {
            tooltip.add(Text.literal(" - ")
                    .append(Text.literal(String.valueOf(slot.amount())))
                    .append(" ")
                    .append(slot.resource().toStack().getName())
                    .formatted(Formatting.GRAY));
        }
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
        if (hit.getSide() != state.get(FACING) || !player.canModifyBlocks() || hand == Hand.OFF_HAND)
            return ActionResult.PASS;
        if (!(world instanceof ServerWorld)) return ActionResult.CONSUME_PARTIAL;
    
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING));
        if (internalPos == null) return ActionResult.PASS;
        var slot = getSlot(internalPos);
        
        var drawer = getBlockEntity(world, pos);
        var playerStack = player.getStackInHand(hand);
        var storage = drawer.storages[slot];
    
        // Upgrade removal
        if (playerStack.isEmpty() && player.isSneaking()) {
            var changeResult = storage.changeUpgrade(null, world, pos, hit.getSide(), player);
            return changeResult ? ActionResult.SUCCESS : ActionResult.FAIL;
        }
    
        var isDoubleClick = DrawerInteractionStatusManager.getAndResetInsertStatus(player, pos, slot);
    
        try (var t = Transaction.openOuter()) {
            int inserted;

            if (isDoubleClick) {
                if (storage.isResourceBlank()) return ActionResult.PASS;
                inserted = (int) StorageUtil.move(PlayerInventoryStorage.of(player), storage, itemVariant -> true, Long.MAX_VALUE, t);
            } else {
                if (playerStack.isEmpty()) return ActionResult.PASS;
                
                inserted = (int) storage.insert(ItemVariant.of(playerStack), playerStack.getCount(), t);
                playerStack.decrement(inserted);
            }
            if (inserted == 0) return ActionResult.CONSUME_PARTIAL;
            
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
            var item = storage.getItem(); // cache because it changes
            var extracted = (int) storage.extract(item, player.isSneaking() ? item.getItem().getMaxCount() : 1, t);
            if (extracted == 0) return;
    
            player.getInventory().offerOrDrop(item.toStack(extracted));
            
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
                    var amount = slot.getAmount();
                    var item = slot.getItem();
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
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return StorageUtil.calculateComparatorOutput(getBlockEntity(world, pos).combinedStorage);
    }
    
    @Override
    public ActionResult toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storages[getSlot(facePos)];
        storage.setLocked(!storage.isLocked());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleVoid(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storages[getSlot(facePos)];
        storage.setVoiding(!storage.isVoiding());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult upgrade(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, PlayerEntity player, ItemStack stack) {
        if (world.isClient) return ActionResult.SUCCESS;
       
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storages[getSlot(facePos)];
    
        if (!(stack.getItem() instanceof UpgradeItem upgrade)) {
            ExtendedDrawers.LOGGER.warn("Expected drawer upgrade to be UpgradeItem but found " + stack.getItem().getClass().getSimpleName() + " instead");
            return ActionResult.FAIL;
        }

        var changed = storage.changeUpgrade(upgrade, world, pos, side, player);
        if (changed)
            stack.decrement(1);

        return changed ? ActionResult.SUCCESS : ActionResult.FAIL;
    }
    
    @Override
    public boolean shouldBlock(World world, BlockPos pos, Direction direction) {
        return world.getBlockState(pos).get(FACING) == direction;
    }
    
    @Override
    public Collection<BlockNode> createNodes() {
        return List.of(new DrawerBlockNode());
    }
}
