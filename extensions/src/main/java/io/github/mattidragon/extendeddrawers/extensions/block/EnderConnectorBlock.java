package io.github.mattidragon.extendeddrawers.extensions.block;

import io.github.mattidragon.extendeddrawers.block.base.NetworkBlockWithEntity;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.EnderConnectorBlockEntity;
import io.github.mattidragon.extendeddrawers.extensions.network.node.EnderConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionBlocks;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class EnderConnectorBlock extends NetworkBlockWithEntity<EnderConnectorBlockEntity> {
    private static final VoxelShape VOXEL_SHAPE = VoxelShapes.union(
            createColumnShape(6, 0, 16),
            VoxelShapes.transform(createColumnShape(6, 0, 16), DirectionTransformation.ROT_90_X_POS),
            VoxelShapes.transform(createColumnShape(6, 0, 16), DirectionTransformation.ROT_90_Z_POS)
    );

    public EnderConnectorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VOXEL_SHAPE;
    }

    @Override
    protected BlockEntityType<EnderConnectorBlockEntity> getType() {
        return ExtensionBlocks.ENDER_CONNECTOR_ENTITY;
    }

    @Override
    public DrawerNetworkBlockNode getNode() {
        return EnderConnectorBlockNode.INSTANCE;
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }
}
