package io.github.mattidragon.extendeddrawers.client.renderer.state;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DrawerRenderState extends BlockEntityRenderState {
    public int slotCount;
    public DrawerSlotRenderState[] slots = new DrawerSlotRenderState[0];
    public BlockState blockState = ModBlocks.SINGLE_DRAWER.defaultBlockState();
    public BlockState facingBlockState = Blocks.AIR.defaultBlockState();
}
