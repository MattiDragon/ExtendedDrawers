package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.CreativeBreakBlocker;
import io.github.mattidragon.extendeddrawers.block.base.DrawerInteractionHandler;
import io.github.mattidragon.extendeddrawers.block.base.NetworkBlockWithEntity;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.DrawerInteractionStatusManager;
import io.github.mattidragon.extendeddrawers.misc.DrawerRaycastUtil;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.network.node.CompactingDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
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
import net.minecraft.registry.Registries;
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

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "deprecation"}) // transfer api and mojank block method deprecation
public class CompactingDrawerBlock extends NetworkBlockWithEntity<CompactingDrawerBlockEntity> implements DrawerInteractionHandler, CreativeBreakBlocker {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public CompactingDrawerBlock(Settings settings) {
        super(settings);
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        var nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt == null || !(world instanceof World realWorld)) return;

        var storageNbt = nbt.getCompound("storage");

        if (ExtendedDrawers.SHIFT_ACCESS.isShiftPressed()) {
            if (Registries.ITEM.get(Identifier.tryParse(storageNbt.getString("upgrade"))) instanceof UpgradeItem upgrade) {
                tooltip.add(upgrade.getName().copy().formatted(Formatting.AQUA));
            }

            var modifierText = Text.empty()
                    .append(Text.literal("V").formatted(storageNbt.getBoolean("voiding") ? Formatting.WHITE : Formatting.DARK_GRAY))
                    .append(Text.literal("L").formatted(storageNbt.getBoolean("locked") ? Formatting.WHITE : Formatting.DARK_GRAY))
                    .append(Text.literal("H").formatted(storageNbt.getBoolean("hidden") ? Formatting.WHITE : Formatting.DARK_GRAY));
            if (storageNbt.getBoolean("duping"))
                modifierText.append(Text.literal("D").formatted(Formatting.WHITE));

            tooltip.add(Text.translatable("tooltip.extended_drawers.modifiers", modifierText).formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.extended_drawers.shift_for_modifiers").formatted(Formatting.GRAY));
        }
        tooltip.add(Text.empty());

        var drawer = new CompactingDrawerBlockEntity(BlockPos.ORIGIN, ModBlocks.COMPACTING_DRAWER.getDefaultState());
        drawer.setWorld(realWorld);
        drawer.readNbt(nbt);
        var storage = drawer.storage;

        var list = Arrays.stream(storage.getActiveSlots())
                .map(slot -> new ResourceAmount<>(slot.getResource(), slot.getTrueAmount()))
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
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            var drawer = getBlockEntity(world, pos);
            if (drawer != null && ExtendedDrawers.CONFIG.get().misc().drawersDropContentsOnBreak()) {
                var slots = drawer.storage.getSlots();
                var amount = drawer.storage.getTrueAmount();
                // Iterate slots in reverse order
                for (int i = slots.length - 1; i >= 0; i--) {
                    var slot = slots[i];
                    if (slot.isBlocked()) continue;

                    var toDrop = amount / slot.getCompression();
                    ItemUtils.offerOrDropStacks(world, pos, null, null, slot.getResource(), toDrop);
                    amount -= toDrop * slot.getCompression();
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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
    protected BlockEntityType<CompactingDrawerBlockEntity> getType() {
        return ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY;
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hit.getSide() != state.get(FACING) || !player.canModifyBlocks() || hand == Hand.OFF_HAND)
            return ActionResult.PASS;
        if (!(world instanceof ServerWorld)) return ActionResult.CONSUME_PARTIAL;
    
        var internalPos = DrawerRaycastUtil.calculateFaceLocation(pos, hit.getPos(), hit.getSide(), state.get(FACING));
        if (internalPos == null) return ActionResult.PASS;

        var drawer = getBlockEntity(world, pos);
        var slot = getSlot(internalPos, drawer.storage.getActiveSlotCount());
        var storage = drawer.storage.getSlot(slot);

        var playerStack = player.getStackInHand(hand);

        // Upgrade & limiter removal
        if (playerStack.isEmpty() && player.isSneaking()) {
            // remove limiter first, if that fails, remove upgrade
            var changeResult = drawer.storage.changeLimiter(ItemVariant.blank(), world, pos, hit.getSide(), player)
                               || drawer.storage.changeUpgrade(ItemVariant.blank(), world, pos, hit.getSide(), player);
            return changeResult ? ActionResult.SUCCESS : ActionResult.FAIL;
        }
    
        var isDoubleClick = DrawerInteractionStatusManager.getAndResetInsertStatus(player, pos, slot);
    
        try (var t = Transaction.openOuter()) {
            int inserted;

            drawer.storage.overrideLock(t);
            if (isDoubleClick) {
                if (storage.getStorage().isBlank()) return ActionResult.PASS;
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
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        var blockEntity = getBlockEntity(world, pos);
        if (blockEntity != null && ExtendedDrawers.CONFIG.get().misc().dropDrawersInCreative() && !world.isClient && player.isCreative() && !blockEntity.isEmpty()) {
            getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, player.getStackInHand(Hand.MAIN_HAND))
                    .forEach(stack -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }

        super.onBreak(world, pos, state, player);
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

        var storage = drawer.storage.getSlot(getSlot(internalPos, drawer.storage.getActiveSlotCount()));
        if (storage.isResourceBlank()) return;
        
        try (var t = Transaction.openOuter()) {
            var item = storage.getResource(); // cache because it changes
            var extracted = (int) storage.extract(item, player.isSneaking() ? item.getItem().getMaxCount() : 1, t);
            if (extracted == 0) return;
    
            player.getInventory().offerOrDrop(item.toStack(extracted));
            
            t.commit();
        }
    }
    
    @SuppressWarnings("DuplicateBranchesInSwitch") // It's clearer like this
    public static int getSlot(Vec2f facePos, int slotCount) {
        int topSlot = switch (slotCount) {
            case 1 -> 0;
            case 2 -> 0;
            case 3 -> 1;
            default -> throw new IllegalStateException("Illegal slot count");
        };
        int leftSlot = switch (slotCount) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 0;
            default -> throw new IllegalStateException("Illegal slot count");
        };
        int rightSlot = switch (slotCount) {
            case 1 -> 2;
            case 2 -> 1;
            case 3 -> 2;
            default -> throw new IllegalStateException("Illegal slot count");
        };

        if (facePos.y < 0.5f) {
            return topSlot;
        } else {
            if (facePos.x < 0.5f) {
                return leftSlot;
            } else {
                return rightSlot;
            }
        }
    }
    
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return StorageUtil.calculateComparatorOutput(getBlockEntity(world, pos).storage);
    }
    
    @Override
    public ActionResult toggleLock(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storage;
        storage.setLocked(!storage.isLocked());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleVoid(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storage;
        storage.setVoiding(!storage.isVoiding());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleHide(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storage;
        storage.setHidden(!storage.isHidden());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult toggleDuping(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side) {
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storage;
        storage.setDuping(!storage.isDuping());
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult changeUpgrade(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, PlayerEntity player, ItemStack stack) {
        if (world.isClient) return ActionResult.SUCCESS;
       
        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storage;
    
        if (!(stack.getItem() instanceof UpgradeItem)) {
            ExtendedDrawers.LOGGER.warn("Expected drawer upgrade to be UpgradeItem but found " + stack.getItem().getClass().getSimpleName() + " instead");
            return ActionResult.FAIL;
        }

        var changed = storage.changeUpgrade(ItemVariant.of(stack), world, pos, side, player);
        if (changed) {
            stack.decrement(1);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    @Override
    public ActionResult changeLimiter(BlockState state, World world, BlockPos pos, Vec3d hitPos, Direction side, PlayerEntity player, ItemStack stack) {
        if (world.isClient) return ActionResult.SUCCESS;

        var facePos = DrawerRaycastUtil.calculateFaceLocation(pos, hitPos, side, state.get(FACING));
        if (facePos == null) return ActionResult.PASS;
        var storage = getBlockEntity(world, pos).storage;

        if (!stack.isOf(ModItems.LIMITER)) {
            ExtendedDrawers.LOGGER.warn("Expected limiter to be limiter but found " + stack + " instead");
            return ActionResult.FAIL;
        }

        var changed = storage.changeLimiter(ItemVariant.of(stack), world, pos, side, player);
        if (changed) {
            stack.decrement(1);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
    
    @Override
    public boolean isFront(BlockState state, Direction direction) {
        return state.get(FACING) == direction;
    }
    
    @Override
    public DrawerNetworkBlockNode getNode() {
        return CompactingDrawerBlockNode.INSTANCE;
    }
}
