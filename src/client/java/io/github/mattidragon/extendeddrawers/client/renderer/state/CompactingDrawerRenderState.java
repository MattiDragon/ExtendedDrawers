package io.github.mattidragon.extendeddrawers.client.renderer.state;

import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import org.jetbrains.annotations.Nullable;

public class CompactingDrawerRenderState extends BlockEntityRenderState {
    public boolean isLocked;
    public boolean isVoiding;
    public boolean isHidden;
    public boolean isDuping;
    public @Nullable UpgradeItem upgrade;
    public boolean hasLimiter;

    public CompactingSlotRenderState[] slots = new CompactingSlotRenderState[3];

    {
        for (var i = 0; i < slots.length; i++) {
            slots[i] = new CompactingSlotRenderState();
        }
    }
}
