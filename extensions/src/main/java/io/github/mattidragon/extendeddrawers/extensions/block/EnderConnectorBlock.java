package io.github.mattidragon.extendeddrawers.extensions.block;

import com.mojang.math.OctahedralGroup;
import io.github.mattidragon.extendeddrawers.block.base.NetworkBlockWithEntity;
import io.github.mattidragon.extendeddrawers.extensions.block.entity.EnderConnectorBlockEntity;
import io.github.mattidragon.extendeddrawers.extensions.network.node.EnderConnectorBlockNode;
import io.github.mattidragon.extendeddrawers.network.node.DrawerNetworkBlockNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnderConnectorBlock extends NetworkBlockWithEntity<EnderConnectorBlockEntity> {
    private static final VoxelShape VOXEL_SHAPE = Shapes.or(
            column(6, 0, 16),
            Shapes.rotate(column(6, 0, 16), OctahedralGroup.ROT_90_X_POS),
            Shapes.rotate(column(6, 0, 16), OctahedralGroup.ROT_90_Z_POS)
    );

    public EnderConnectorBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
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
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }
}
