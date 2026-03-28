package io.github.mattidragon.extendeddrawers.storage;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.entity.StorageDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.component.DrawerSlotComponent;
import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import io.github.mattidragon.extendeddrawers.item.LimiterItem;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import io.github.mattidragon.extendeddrawers.misc.ItemUtils;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public interface ModifierDrawerStorage extends ModifierAccess, DrawerStorage {
    StorageDrawerBlockEntity getOwner();

    @Override
    default boolean changeUpgrade(ItemVariant newUpgrade, Level level, BlockPos pos, Direction side, @Nullable Player player) {
        if (!(newUpgrade.getItem() instanceof UpgradeItem) && !newUpgrade.isBlank()) return false;

        var oldUpgrade = settings().upgrade;
        if (newUpgrade.isBlank() && oldUpgrade.isBlank()) return false;

        settings().upgrade = newUpgrade;
        if (getTrueAmount() > getCapacity() && ExtendedDrawers.CONFIG.get().misc().blockUpgradeRemovalsWithOverflow()) {
            settings().upgrade = oldUpgrade;
            if (player != null)
                player.sendOverlayMessage(Component.translatable("extended_drawer.drawer.upgrade_fail"));
            return false;
        }

        ItemUtils.offerOrDrop(level, pos, side, player, oldUpgrade.toStack());
        dumpExcess(level, pos, side, player);
        return true;
    }

    @Override
    default boolean changeLimiter(ItemVariant newLimiter, Level level, BlockPos pos, Direction side, @Nullable Player player) {
        if (!(newLimiter.getItem() instanceof LimiterItem) && !newLimiter.isBlank()) return false;

        var oldLimiter = settings().limiter;
        if (newLimiter.isBlank() && oldLimiter.isBlank()) return false;

        settings().limiter = newLimiter;
        if (getTrueAmount() > getCapacity() && ExtendedDrawers.CONFIG.get().misc().blockUpgradeRemovalsWithOverflow()) {
            settings().limiter = oldLimiter;
            if (player != null)
                player.sendOverlayMessage(Component.translatable("extended_drawer.drawer.limiter_fail"));
            return false;
        }

        ItemUtils.offerOrDrop(level, pos, side, player, oldLimiter.toStack());
        dumpExcess(level, pos, side, player);
        return true;
    }

    default void update() {
        getOwner().onSlotChanged(settings().sortingDirty);
        settings().sortingDirty = false;
    }

    void dumpExcess(Level level, BlockPos pos, @Nullable Direction side, @Nullable Player player);

    default void readData(ValueInput input) {
        settings().locked = input.getBooleanOr("locked", false);
        settings().voiding = input.getBooleanOr("voiding", false);
        settings().hidden = input.getBooleanOr("hidden", false);
        settings().duping = input.getBooleanOr("duping", false);
        settings().upgrade = input.read("capacityUpgrade", ItemVariant.CODEC).orElseGet(ItemVariant::blank);
        settings().limiter = input.read("limiter", ItemVariant.CODEC).orElseGet(ItemVariant::blank);
    }

    default void writeData(ValueOutput output) {
        output.putBoolean("locked", settings().locked);
        output.putBoolean("voiding", settings().voiding);
        output.putBoolean("hidden", settings().hidden);
        output.putBoolean("duping", settings().duping);
        output.store("capacityUpgrade", ItemVariant.CODEC, settings().upgrade);
        output.store("limiter", ItemVariant.CODEC, settings().limiter);
    }

    /**
     * Temporarily overrides the lock of the slot for inserting.
     * Used by {@link io.github.mattidragon.extendeddrawers.block.DrawerBlock#useWithoutItem DrawerBlock#onUse} to allow adding items to locked drawers manually.
     * Should not be used multiple times within the same transaction.
     * @param transaction The transaction for which the lock stays overridden. When closed
     */
    @Override
    default void overrideLock(TransactionContext transaction) {
        if (settings().lockOverridden) {
            ExtendedDrawers.LOGGER.warn("Tried to override drawer lock while already overridden. Unexpected behavior may follow.");
            return;
        }
        transaction.addCloseCallback((_, _) -> settings().lockOverridden = false);
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
        return settings().limiter.getComponents().getOrDefault(ModDataComponents.LIMITER_LIMIT, LimiterLimitComponent.NO_LIMIT).limit();
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

    Settings settings();

    long getCapacity();

    /**
     * True amount value without duping mode changes
     */
    long getTrueAmount();

    class Settings {
        ItemVariant upgrade = ItemVariant.blank();
        ItemVariant limiter = ItemVariant.blank();

        boolean locked = false;
        boolean hidden = false;
        boolean voiding = false;
        boolean duping = false;

        boolean lockOverridden = false;
        boolean sortingDirty = false;

        public void readComponent(DrawerSlotComponent component) {
            upgrade = component.upgrade();
            limiter = component.limiter();
            locked = component.locked();
            hidden = component.hidden();
            voiding = component.voiding();
            duping = component.duping();
        }
    }
}
