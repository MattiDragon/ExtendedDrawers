package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.CompactingDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.network.node.CompactingDrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.storage.CompactingDrawerStorage;
import io.github.mattidragon.extendeddrawers.storage.ModifierAccess;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;

public class CompactingDrawerBlock extends StorageDrawerBlock<CompactingDrawerBlockEntity> {
    public CompactingDrawerBlock(Settings settings) {
        super(settings);
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
