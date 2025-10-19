package io.github.mattidragon.extendeddrawers.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import org.jetbrains.annotations.NotNull;

public interface DrawerStorage extends Comparable<DrawerStorage>, Storage<ItemVariant> {
    boolean isBlank();

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

    boolean isLocked();

    boolean isVoiding();

    boolean isHidden();

    boolean isDuping();

    boolean hasLimiter();
}
