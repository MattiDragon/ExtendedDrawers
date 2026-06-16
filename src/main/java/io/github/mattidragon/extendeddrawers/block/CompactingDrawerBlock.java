package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.network.node.CompactingDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.ModifierAccess;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

public class CompactingDrawerBlock extends StorageDrawerBlock<CompactingDrawerBlockEntity> {
    public CompactingDrawerBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected BlockEntityType<CompactingDrawerBlockEntity> getType() {
        return ModBlocks.COMPACTING_DRAWER_BLOCK_ENTITY;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        var drawer = getBlockEntity(level, pos);
        if (drawer == null) return 0;
        return StorageUtil.getRedstoneSignal(drawer.storage);
    }

    @Override
    protected ModifierAccess getModifierAccess(CompactingDrawerBlockEntity drawer, Vec2 facePos) {
        return drawer.storage;
    }

    @SuppressWarnings("DuplicateBranchesInSwitch") // It's clearer like this
    public int getSlotIndex(CompactingDrawerBlockEntity drawer, Vec2 facePos) {
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
