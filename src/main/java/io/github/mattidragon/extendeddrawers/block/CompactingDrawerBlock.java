package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.network.node.CompactingDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.ModifierAccess;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;

import java.util.List;

public class CompactingDrawerBlock extends StorageDrawerBlock<CompactingDrawerBlockEntity> {
    public CompactingDrawerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        var component = stack.get(ModDataComponents.COMPACTING_DRAWER_CONTENTS);
        if (component == null) return;
        
        if (ExtendedDrawers.SHIFT_ACCESS.isShiftPressed()) {
            if (component.upgrade().getItem() instanceof UpgradeItem upgrade) {
                tooltip.add(upgrade.getName().copy().formatted(Formatting.AQUA));
            }

            var modifierText = Text.empty()
                    .append(Text.literal("V").formatted(component.voiding() ? Formatting.WHITE : Formatting.DARK_GRAY))
                    .append(Text.literal("L").formatted(component.locked() ? Formatting.WHITE : Formatting.DARK_GRAY))
                    .append(Text.literal("H").formatted(component.hidden() ? Formatting.WHITE : Formatting.DARK_GRAY));
            if (component.duping()) {
                modifierText.append(Text.literal("D").formatted(Formatting.WHITE));
            }

            tooltip.add(Text.translatable("tooltip.extended_drawers.modifiers", modifierText).formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("tooltip.extended_drawers.shift_for_modifiers").formatted(Formatting.GRAY));
        }
        tooltip.add(Text.empty());

        tooltip.add(Text.translatable("tooltip.extended_drawers.drawer_contents").formatted(Formatting.GRAY));
        tooltip.add(Text.literal(" - ")
                .append(Text.literal(String.valueOf(component.amount())))
                .append(" ")
                .append(component.item().toStack().getName())
                .formatted(Formatting.GRAY));

    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            var drawer = getBlockEntity(world, pos);
            if (drawer != null && ExtendedDrawers.CONFIG.get().misc().drawersDropContentsOnBreak()) {
                var slots = drawer.storage.getSlotArray();
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
