package io.github.mattidragon.extendeddrawers.compacting;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.World;

public interface CompressionRecipeManager {
    CompressionLadder getLadder(ItemVariant item, World world);
    
    static CompressionRecipeManager of(World world) {
        return ((Provider) world).extended_drawers$getCompactingManager();
    }
    
    interface Provider {
        CompressionRecipeManager extended_drawers$getCompactingManager();
    }
}
