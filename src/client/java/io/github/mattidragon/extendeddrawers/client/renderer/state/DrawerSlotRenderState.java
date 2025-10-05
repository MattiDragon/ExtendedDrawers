package io.github.mattidragon.extendeddrawers.client.renderer.state;

import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.minecraft.client.render.item.ItemRenderState;
import org.jetbrains.annotations.Nullable;

public class DrawerSlotRenderState {
    public boolean isLocked;
    public boolean isVoiding;
    public boolean isHidden;
    public boolean isDuping;
    public @Nullable UpgradeItem upgrade;
    public boolean hasLimiter;

    public ItemRenderState item = new ItemRenderState();
    public long amount;
}
