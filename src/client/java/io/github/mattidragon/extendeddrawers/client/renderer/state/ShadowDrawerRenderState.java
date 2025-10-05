package io.github.mattidragon.extendeddrawers.client.renderer.state;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

public class ShadowDrawerRenderState extends BlockEntityRenderState {
    public boolean isHidden;

    public ItemRenderState item = new ItemRenderState();
    public long count;
}
