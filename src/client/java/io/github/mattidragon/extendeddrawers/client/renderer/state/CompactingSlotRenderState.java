package io.github.mattidragon.extendeddrawers.client.renderer.state;

import net.minecraft.client.renderer.item.ItemStackRenderState;

public class CompactingSlotRenderState {
    public boolean disabled;

    public ItemStackRenderState item = new ItemStackRenderState();
    public long amount;
}
