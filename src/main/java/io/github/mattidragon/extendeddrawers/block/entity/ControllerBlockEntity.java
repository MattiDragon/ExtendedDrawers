package io.github.mattidragon.extendeddrawers.block.entity;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class ControllerBlockEntity extends BlockEntity {
    public long lastInsertTimestamp = -1;
    public ItemVariant lastInsertType = null;
    
    public ControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CONTROLLER_BLOCK_ENTITY, pos, state);
    }
}
