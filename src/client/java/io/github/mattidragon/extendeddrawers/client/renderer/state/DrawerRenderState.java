package io.github.mattidragon.extendeddrawers.client.renderer.state;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;

public class DrawerRenderState extends BlockEntityRenderState {
    public int slotCount;
    public DrawerSlotRenderState[] slots;
}
