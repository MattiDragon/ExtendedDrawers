package io.github.mattidragon.extendeddrawers.client.renderer.state;

import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jspecify.annotations.Nullable;

public class DrawerSlotRenderState {
    public boolean isLocked;
    public boolean isVoiding;
    public boolean isHidden;
    public boolean isDuping;
    public @Nullable UpgradeItem upgrade;
    public boolean hasLimiter;

    public final ItemStackRenderState item = new ItemStackRenderState();
    public long amount;
}
