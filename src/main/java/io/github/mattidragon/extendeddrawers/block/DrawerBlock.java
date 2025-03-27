package io.github.mattidragon.extendeddrawers.block;

import io.github.mattidragon.extendeddrawers.block.base.StorageDrawerBlock;
import io.github.mattidragon.extendeddrawers.block.entity.DrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.network.node.DrawerBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import io.github.mattidragon.extendeddrawers.storage.DrawerSlot;
import io.github.mattidragon.extendeddrawers.storage.ModifierAccess;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;

public class DrawerBlock extends StorageDrawerBlock<DrawerBlockEntity> {
    public final int slots;

    public DrawerBlock(Settings settings, int slots) {
        super(settings);
        this.slots = slots;
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
