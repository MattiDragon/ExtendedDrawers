package io.github.mattidragon.extendeddrawers.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ShadowDrawerRenderState extends BlockEntityRenderState {
    public boolean isHidden;

    public ItemStackRenderState item = new ItemStackRenderState();
    public long count;
}
