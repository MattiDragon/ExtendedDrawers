package io.github.mattidragon.extendeddrawers.client.renderer.state;

import net.minecraft.client.render.item.ItemRenderState;

public class CompactingSlotRenderState {
    public boolean disabled;

    public ItemRenderState item = new ItemRenderState();
    public long amount;
}
