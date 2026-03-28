package io.github.mattidragon.extendeddrawers.compacting;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.level.Level;

public interface CompressionRecipeManager {
    CompressionLadder getLadder(ItemVariant item, Level level);
    
    static CompressionRecipeManager of(Level level) {
        return ((Provider) level).extended_drawers$getCompactingManager();
    }
    
    interface Provider {
        CompressionRecipeManager extended_drawers$getCompactingManager();
    }
}
