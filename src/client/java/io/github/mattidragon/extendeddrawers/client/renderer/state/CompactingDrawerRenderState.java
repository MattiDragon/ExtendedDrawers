package io.github.mattidragon.extendeddrawers.client.renderer.state;

import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.registry.ModBlocks;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CompactingDrawerRenderState extends BlockEntityRenderState {
    public boolean isLocked;
    public boolean isVoiding;
    public boolean isHidden;
    public boolean isDuping;
    public @Nullable UpgradeItem upgrade;
    public boolean hasLimiter;
    public BlockState blockState = ModBlocks.COMPACTING_DRAWER.defaultBlockState();

    public final CompactingSlotRenderState[] slots = new CompactingSlotRenderState[3];

    {
        for (var i = 0; i < slots.length; i++) {
            slots[i] = new CompactingSlotRenderState();
        }
    }
}
