package io.github.mattidragon.extendeddrawers.client.renderer.state;

import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class ShadowDrawerRenderState extends BlockEntityRenderState {
    public boolean isHidden;

    public final ItemStackRenderState item = new ItemStackRenderState();
    public long count;

    public BlockState blockState = ModBlocks.SHADOW_DRAWER.defaultBlockState();
}
