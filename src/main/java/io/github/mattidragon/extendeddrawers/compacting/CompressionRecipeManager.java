package io.github.mattidragon.extendeddrawers.compacting;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.level.Level;

public interface CompressionRecipeManager {
    CompressionLadder getLadder(ItemVariant item, Level world);
    
    static CompressionRecipeManager of(Level world) {
        return ((Provider) world).extended_drawers$getCompactingManager();
    }
    
    interface Provider {
        CompressionRecipeManager extended_drawers$getCompactingManager();
    }
}
