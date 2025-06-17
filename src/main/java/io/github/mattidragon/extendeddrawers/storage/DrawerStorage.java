package io.github.mattidragon.extendeddrawers.storage;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.entity.StorageDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import io.github.mattidragon.extendeddrawers.item.LimiterItem;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface DrawerStorage extends Comparable<DrawerStorage>, Storage<ItemVariant>, ModifierAccess permits DrawerSlot, CompactingDrawerStorage {
    StorageDrawerBlockEntity getOwner();

    Settings settings();

    boolean isBlank();

    long getCapacity();

    /**
     * True amount value without duping mode changes
     */
    long getTrueAmount();

    @Override
    default boolean changeUpgrade(ItemVariant newUpgrade, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player) {
        if (!(newUpgrade.getItem() instanceof UpgradeItem) && !newUpgrade.isBlank()) return false;

        var oldUpgrade = settings().upgrade;
        if (newUpgrade.isBlank() && oldUpgrade.isBlank()) return false;

        settings().upgrade = newUpgrade;
        if (getTrueAmount() > getCapacity() && ExtendedDrawers.CONFIG.get().misc().blockUpgradeRemovalsWithOverflow()) {
            settings().upgrade = oldUpgrade;
            if (player != null)
                player.sendMessage(Text.translatable("extended_drawer.drawer.upgrade_fail"), true);
            return false;
        }

        ItemUtils.offerOrDrop(world, pos, side, player, oldUpgrade.toStack());
        dumpExcess(world, pos, side, player);
        return true;
    }

    @Override
    default boolean changeLimiter(ItemVariant newLimiter, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player) {
        if (!(newLimiter.getItem() instanceof LimiterItem) && !newLimiter.isBlank()) return false;

        var oldLimiter = settings().limiter;
        if (newLimiter.isBlank() && oldLimiter.isBlank()) return false;

        settings().limiter = newLimiter;
        if (getTrueAmount() > getCapacity() && ExtendedDrawers.CONFIG.get().misc().blockUpgradeRemovalsWithOverflow()) {
            settings().limiter = oldLimiter;
            if (player != null)
                player.sendMessage(Text.translatable("extended_drawer.drawer.limiter_fail"), true);
            return false;
        }

        ItemUtils.offerOrDrop(world, pos, side, player, oldLimiter.toStack());
        dumpExcess(world, pos, side, player);
        return true;
    }

    default void update() {
        getOwner().onSlotChanged(settings().sortingDirty);
        settings().sortingDirty = false;
    }

    @Override
    default int compareTo(@NotNull DrawerStorage other) {
        if (this.isBlank() != other.isBlank())
            return this.isBlank() ? 1 : -1;
        if (this.isLocked() != other.isLocked())
            return this.isLocked() ? -1 : 1;
        if (this.isVoiding() != other.isVoiding())
            return this.isVoiding() ? 1 : -1;

        return 0;
    }

    void dumpExcess(World world, BlockPos pos, @Nullable Direction side, @Nullable PlayerEntity player);

    default void readData(ReadView view) {
        settings().locked = view.getBoolean("locked", false);
        settings().voiding = view.getBoolean("voiding", false);
        settings().hidden = view.getBoolean("hidden", false);
        settings().duping = view.getBoolean("duping", false);
        settings().upgrade = view.read("capacityUpgrade", ItemVariant.CODEC).orElseGet(ItemVariant::blank);
        settings().limiter = view.read("limiter", ItemVariant.CODEC).orElseGet(ItemVariant::blank);
    }

    default void writeData(WriteView view) {
        view.putBoolean("locked", settings().locked);
        view.putBoolean("voiding", settings().voiding);
        view.putBoolean("hidden", settings().hidden);
        view.putBoolean("duping", settings().duping);
        view.put("capacityUpgrade", ItemVariant.CODEC, settings().upgrade);
        view.put("limiter", ItemVariant.CODEC, settings().limiter);
    }

    /**
     * Temporarily overrides the lock of the slot for inserting.
     * Used by {@link io.github.mattidragon.extendeddrawers.block.DrawerBlock#onUse DrawerBlock#onUse} to allow adding items to locked drawers manually.
     * Should not be used multiple times within the same transaction.
     * @param transaction The transaction for which the lock stays overridden. When closed
     */
    @Override
    default void overrideLock(TransactionContext transaction) {
        if (settings().lockOverridden) {
            ExtendedDrawers.LOGGER.warn("Tried to override drawer lock while already overridden. Unexpected behavior may follow.");
            return;
        }
        transaction.addCloseCallback((transaction1, result) -> settings().lockOverridden = false);
        settings().lockOverridden = true;
    }

    @Override
    default boolean isLocked() {
        return settings().locked;
    }

    @Override
    default boolean isVoiding() {
        return settings().voiding;
    }

    @Override
    default boolean isHidden() {
        return settings().hidden;
    }

    @Override
    default boolean isDuping() {
        return settings().duping;
    }

    @Override
    @Nullable
    default UpgradeItem getUpgrade() {
        return settings().upgrade.getItem() instanceof UpgradeItem upgrade ? upgrade : null;
    }

    @Override
    default long getLimiter() {
        return settings().limiter.getComponentMap().getOrDefault(ModDataComponents.LIMITER_LIMIT, LimiterLimitComponent.NO_LIMIT).limit();
    }

    @Override
    default void setLocked(boolean locked) {
        settings().sortingDirty = true;
        settings().locked = locked;
        update();
    }

    @Override
    default void setVoiding(boolean voiding) {
        settings().sortingDirty = true;
        settings().voiding = voiding;
        update();
    }

    @Override
    default void setHidden(boolean hidden) {
        settings().hidden = hidden;
        update();
    }

    @Override
    default void setDuping(boolean duping) {
        settings().duping = duping;
        update();
    }

    @Override
    default boolean hasLimiter() {
        return !settings().limiter.isBlank();
    }

    class Settings {
        ItemVariant upgrade = ItemVariant.blank();
        ItemVariant limiter = ItemVariant.blank();

        boolean locked = false;
        boolean hidden = false;
        boolean voiding = false;
        boolean duping = false;

        boolean lockOverridden = false;
        boolean sortingDirty = false;
    }
}
