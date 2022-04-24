package io.github.mattidragon.extendeddrawers.util;

import io.github.mattidragon.extendeddrawers.config.CommonConfig;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.WeakHashMap;

@SuppressWarnings("ALL")
public final class DrawerInteractionStatusManager {
    private DrawerInteractionStatusManager(){}
    
    // Using ThreadLocal to separate server and client
    private static final ThreadLocal<WeakHashMap<PlayerEntity, Interaction>> INSERTIONS = ThreadLocal.withInitial(WeakHashMap::new);
    private static final ThreadLocal<WeakHashMap<PlayerEntity, Long>> EXTRACTIONS = ThreadLocal.withInitial(WeakHashMap::new);
    
    /**
     * Provides whether the player should insert one or all stacks and updates internal counters to match that. Ensures that double clicks only cause multi-stack insertion when the clicks are on the same slot.
     * @param player The player performing the insertion
     * @param pos The position of the drawer
     * @param slot The slot that was interacted with
     * @param item The type of item inserted
     * @return The item that should be used for the insertion or an empty optional if there shouldn't be a multi-stack insertion.
     */
    public static Optional<ItemVariant> getAndResetInsertStatus(PlayerEntity player, BlockPos pos, int slot, ItemVariant item) {
        var timestamp = player.getWorld().getTime();
        var interaction = INSERTIONS.get().remove(player);
        if (interaction != null)
            if (interaction.pos.equals(pos) && timestamp - interaction.timestamp < CommonConfig.HANDLE.get().insertAllTime() && interaction.slot == slot)
                return Optional.of(interaction.item);
            else
                return Optional.empty();
            
        INSERTIONS.get().put(player, new Interaction(timestamp, pos, slot, item));
        return Optional.empty();
    }
    
    /**
     * Makes sure that players can only extract every four ticks to avoid double extractions caused by changing the held item.
     * @param player The player attempting extraction
     * @return Whether the player should be allowed to extract.
     */
    public static boolean getAndResetExtractionTimer(PlayerEntity player) {
        if (!CommonConfig.HANDLE.get().deduplicateExtraction()) return true;
        
        var time = player.getWorld().getTime();
        var timestamp = EXTRACTIONS.get().remove(player);
        if (timestamp != null && time - timestamp <= 3) return false;
        
        EXTRACTIONS.get().put(player, time);
        return true;
    }
    
    private record Interaction(long timestamp, BlockPos pos, int slot, ItemVariant item) {}
}
