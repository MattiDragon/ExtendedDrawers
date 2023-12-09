package io.github.mattidragon.extendeddrawers.storage;

import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ModifierAccess {
    boolean changeUpgrade(ItemVariant newUpgrade, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player);

    boolean changeLimiter(ItemVariant newLimiter, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player);

    /**
     * Temporarily overrides the lock of the slot for inserting.
     * Used by {@link io.github.mattidragon.extendeddrawers.block.DrawerBlock#onUse DrawerBlock#onUse} to allow adding items to locked drawers manually.
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
