package io.github.mattidragon.extendeddrawers.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class DrawerRenderState extends BlockEntityRenderState {
    public int slotCount;
    public DrawerSlotRenderState[] slots = new DrawerSlotRenderState[0];
}
