package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.network.node.DrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.storage.DrawerSlot;
import io.github.mattidragon.extendeddrawers.storage.ModifierAccess;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

public class DrawerBlock extends StorageDrawerBlock<DrawerBlockEntity> {
    public final int slots;

    public DrawerBlock(Properties settings, int slots) {
        super(settings);
        this.slots = slots;
    }

    @Override
    protected BlockEntityType<DrawerBlockEntity> getType() {
        return ModBlocks.DRAWER_BLOCK_ENTITY;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        var drawer = getBlockEntity(level, pos);
        if (drawer == null) return 0;
        return StorageUtil.getRedstoneSignal(drawer.combinedStorage);
    }

    @Override
    protected ModifierAccess getModifierAccess(DrawerBlockEntity drawer, Vec2 facePos) {
        return getSlot(drawer, getSlotIndex(drawer, facePos));
    }

    @Override
    public int getSlotIndex(DrawerBlockEntity drawer, Vec2 facePos) {
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
