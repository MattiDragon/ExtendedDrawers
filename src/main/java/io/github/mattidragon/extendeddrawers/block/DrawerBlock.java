package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.network.node.DrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.storage.DrawerSlot;
import io.github.mattidragon.extendeddrawers.storage.ModifierAccess;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DrawerBlock extends StorageDrawerBlock<DrawerBlockEntity> {
    public final int slots;

    public DrawerBlock(Settings settings, int slots) {
        super(settings);
        this.slots = slots;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView blockView, List<Text> tooltip, TooltipContext context) {
        var nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt == null) return;

        var list = nbt.getList("items", NbtElement.COMPOUND_TYPE).stream()
                .map(NbtCompound.class::cast)
                .map(data -> {
                    var slot = new DrawerSlot(null, 1);
                    slot.readNbt(data);
                    return slot;
                })
                .filter(slot -> !slot.isBlank() || slot.getUpgrade() != null || slot.isHidden() || slot.isLocked() || slot.isVoiding())
                .toList();
        if (list.isEmpty()) return;
        boolean shift = ExtendedDrawers.SHIFT_ACCESS.isShiftPressed();

        if (!shift) {
            tooltip.add(Text.translatable("tooltip.extended_drawers.shift_for_modifiers").formatted(Formatting.GRAY));
            tooltip.add(Text.empty());
        }

        if (!list.stream().allMatch(DrawerSlot::isBlank) || shift)
            tooltip.add(Text.translatable("tooltip.extended_drawers.drawer_contents").formatted(Formatting.GRAY));
        for (var slot : list) {
            MutableText text;
            if (!slot.isBlank()) {
                text = Text.literal(" - ");
                text.append(Text.literal(String.valueOf(slot.getTrueAmount())))
                        .append(" ")
                        .append(slot.getResource().toStack().getName());
            } else if (shift) {
                text = Text.literal(" - ");
                text.append(Text.translatable("tooltip.extended_drawers.empty").formatted(Formatting.ITALIC));
            } else continue;

            // Seems like client code is safe here. If this breaks then other mods are broken too.
            if (shift) {
                text.append("  ")
                        .append(Text.literal("V").formatted(slot.isVoiding() ? Formatting.WHITE : Formatting.DARK_GRAY))
                        .append(Text.literal("L").formatted(slot.isLocked() ? Formatting.WHITE : Formatting.DARK_GRAY))
                        .append(Text.literal("H").formatted(slot.isHidden() ? Formatting.WHITE : Formatting.DARK_GRAY));
                if (slot.isDuping())
                    text.append(Text.literal("D").formatted(Formatting.WHITE));

                if (slot.getUpgrade() != null) {
                    text.append(" ").append(slot.getUpgrade().getName().copy().formatted(Formatting.AQUA));
                }
            }
            tooltip.add(text.formatted(Formatting.GRAY));
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            var drawer = getBlockEntity(world, pos);
            if (drawer != null && ExtendedDrawers.CONFIG.get().misc().drawersDropContentsOnBreak()) {
                for (var slot : drawer.storages) {
                    ItemUtils.offerOrDropStacks(world, pos, null, null, slot.getResource(), slot.getAmount());
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected BlockEntityType<DrawerBlockEntity> getType() {
        return ModBlocks.DRAWER_BLOCK_ENTITY;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        var drawer = getBlockEntity(world, pos);
        if (drawer == null) return 0;
        return StorageUtil.calculateComparatorOutput(drawer.combinedStorage);
    }

    @Override
    protected ModifierAccess getModifierAccess(DrawerBlockEntity drawer, Vec2f facePos) {
        return getSlot(drawer, getSlotIndex(drawer, facePos));
    }

    @Override
    public int getSlotIndex(DrawerBlockEntity drawer, Vec2f facePos) {
        return switch (slots) {
            case 1 -> 0;
            case 2 -> facePos.x < 0.5f ? 0 : 1;
            case 4 -> facePos.y < 0.5f ? facePos.x < 0.5f ? 0 : 1 : facePos.x < 0.5f ? 2 : 3;
            default -> throw new IllegalStateException("unexpected drawer slot count");
        };
    }

    @Override
    public DrawerSlot getSlot(DrawerBlockEntity drawer, int slot) {
        return drawer.storages[slot];
    }

    @Override
    public DrawerNetworkBlockNode getNode() {
        return DrawerBlockNode.INSTANCE;
    }
}
