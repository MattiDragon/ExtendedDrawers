package io.github.mattidragon.extendeddrawers.storage;

import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public interface ModifierAccess {
    boolean changeUpgrade(ItemVariant newUpgrade, Level level, BlockPos pos, Direction side, @Nullable Player player);

    boolean changeLimiter(ItemVariant newLimiter, Level level, BlockPos pos, Direction side, @Nullable Player player);

    /**
     * Temporarily overrides the lock of the slot for inserting.
     * Used by {@link io.github.mattidragon.extendeddrawers.block.DrawerBlock#useWithoutItem DrawerBlock#onUse} to allow adding items to locked drawers manually.
     * Should not be used multiple times within the same transaction.
     * @param transaction The transaction for which the lock stays overridden. When closed
     */
    void overrideLock(TransactionContext transaction);

    boolean isLocked();

    boolean isVoiding();

    boolean isHidden();

    boolean isDuping();

    @Nullable UpgradeItem getUpgrade();

    long getLimiter();

    void setLocked(boolean locked);

    void setVoiding(boolean voiding);

    void setHidden(boolean hidden);

    void setDuping(boolean duping);

    boolean hasLimiter();
}
