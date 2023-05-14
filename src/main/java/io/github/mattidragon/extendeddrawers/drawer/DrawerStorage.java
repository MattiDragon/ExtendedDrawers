package io.github.mattidragon.extendeddrawers.drawer;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.block.entity.StorageDrawerBlockEntity;
import io.github.mattidragon.extendeddrawers.item.UpgradeItem;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public sealed interface DrawerStorage extends Comparable<DrawerStorage>, Storage<ItemVariant> permits DrawerSlot, CompactingDrawerStorage {
    boolean changeUpgrade(@Nullable UpgradeItem newUpgrade, World world, BlockPos pos, Direction side, @Nullable PlayerEntity player);

    StorageDrawerBlockEntity getOwner();

    Settings settings();

    boolean isBlank();

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

    default void readNbt(NbtCompound nbt) {
        settings().locked = nbt.getBoolean("locked");
        settings().voiding = nbt.getBoolean("voiding");
        settings().hidden = nbt.getBoolean("hidden");
        settings().upgrade = Registry.ITEM.get(Identifier.tryParse(nbt.getString("upgrade"))) instanceof UpgradeItem upgrade ? upgrade : null;
    }

    default void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("locked", settings().locked);
        nbt.putBoolean("voiding", settings().voiding);
        nbt.putBoolean("hidden", settings().hidden);
        nbt.putString("upgrade", Registry.ITEM.getId(settings().upgrade).toString());
    }

    /**
     * Temporarily overrides the lock of the slot for inserting.
     * Used by {@link io.github.mattidragon.extendeddrawers.block.DrawerBlock#onUse DrawerBlock#onUse} to allow adding items to locked drawers manually.
     * Should not be used multiple times within the same transaction.
     * @param transaction The transaction for which the lock stays overridden. When closed
     */
    default void overrideLock(TransactionContext transaction) {
        if (settings().lockOverridden) {
            ExtendedDrawers.LOGGER.warn("Tried to override drawer lock while already overridden. Unexpected behavior may follow.");
            return;
        }
        transaction.addCloseCallback((transaction1, result) -> settings().lockOverridden = false);
        settings().lockOverridden = true;
    }

    default boolean isLocked() {
        return settings().locked;
    }

    default boolean isVoiding() {
        return settings().voiding;
    }

    default boolean isHidden() {
        return settings().hidden;
    }

    @Nullable
    default UpgradeItem getUpgrade() {
        return settings().upgrade;
    }

    default void setLocked(boolean locked) {
        settings().sortingDirty = true;
        settings().locked = locked;
        update();
    }

    default void setVoiding(boolean voiding) {
        settings().sortingDirty = true;
        settings().voiding = voiding;
        update();
    }

    default void setHidden(boolean hidden) {
        settings().hidden = hidden;
        update();
    }

    class Settings {
        @Nullable
        UpgradeItem upgrade = null;
        boolean locked = false;
        boolean lockOverridden = false;
        boolean hidden = false;
        boolean voiding = false;
        boolean sortingDirty = false;
    }
}
