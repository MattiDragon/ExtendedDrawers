package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.network.node.CompactingDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.ModifierAccess;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CompactingDrawerBlock extends StorageDrawerBlock<CompactingDrawerBlockEntity> {
    public CompactingDrawerBlock(Settings settings) {
        super(settings);
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
    protected BlockEntityType<CompactingDrawerBlockEntity> getType() {
        return ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return 0;
        return StorageUtil.calculateComparatorOutput(drawer.storage);
    }

    @Override
    protected ModifierAccess getModifierAccess(CompactingDrawerBlockEntity drawer, Vec2f facePos) {
        return drawer.storage;
    }

    @SuppressWarnings("DuplicateBranchesInSwitch") // It's clearer like this
    public int getSlotIndex(CompactingDrawerBlockEntity drawer, Vec2f facePos) {
        var slotCount = drawer.storage.getActiveSlotCount();
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
    public CompactingDrawerStorage.Slot getSlot(CompactingDrawerBlockEntity drawer, int slot) {
        return drawer.storage.getSlot(slot);
    }

    @Override
    public DrawerNetworkBlockNode getNode() {
        return CompactingDrawerBlockNode.INSTANCE;
    }
}
