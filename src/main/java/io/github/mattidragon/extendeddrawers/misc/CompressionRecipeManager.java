package io.github.mattidragon.extendeddrawers.misc;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public final class CompressionRecipeManager {
    private final RecipeManager recipeManager;
    private final Map<ItemVariant, CompressionLadder> ladders = new HashMap<>();

    public CompressionRecipeManager(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public void reload() {
        ladders.clear();
    }

    public CompressionLadder getLadder(ItemVariant item, World world) {
        if (ladders.containsKey(item))
            return ladders.get(item);
        var ladder = buildLadder(item, world);
        // Put ladder in map for all items
        ladder.steps.forEach(step -> ladders.put(step.item, ladder));
        return ladder;
    }

    private CompressionLadder buildLadder(ItemVariant item, World world) {
        var bottom = findBottom(item, world);
        var ladder = new ArrayList<CompressionLadder.Step>();
        var currentItem = bottom;
        var currentSize = 1;
        ladder.add(new CompressionLadder.Step(currentItem, currentSize));

        while (true) {
            var pair = findCompressionRecipe(currentItem, world);
            if (pair == null) break;
            currentItem = pair.compressed;
            currentSize *= pair.scale;
            ladder.add(new CompressionLadder.Step(currentItem, currentSize));
        }
        return new CompressionLadder(List.copyOf(ladder));
    }

    private ItemVariant findBottom(ItemVariant item, World world) {
        // Detect cycles just in case
        var visited = new HashSet<ItemVariant>();
        var candidate = item;
        while (true) {
            var pair = findDecompressionRecipe(candidate, world);
            if (pair == null) break; // Reached bottom
            if (visited.contains(pair.decompressed)) break; // Next item would be a cycle, this is the bottom now
            candidate = pair.decompressed;
            visited.add(candidate);
        }
        return candidate;
    }

    private RecipePair findCompressionRecipe(ItemVariant decompressed, World world) {
        var largeCompressionRecipe = findCompressionRecipeForSize(decompressed, world, true);
        if (largeCompressionRecipe != null) return largeCompressionRecipe;

        return findCompressionRecipeForSize(decompressed, world, false);
    }

    @Nullable
    private RecipePair findCompressionRecipeForSize(ItemVariant decompressed, World world, boolean isLarge) {
        var largeCompressionInventory = createInventory(decompressed.toStack(), isLarge ? 3 : 2);
        var largeCompressionRecipe = recipeManager.getFirstMatch(RecipeType.CRAFTING, largeCompressionInventory, world);
        if (largeCompressionRecipe.isEmpty()) return null;
        var largeCompressionResult = largeCompressionRecipe.get().craft(largeCompressionInventory);
        var decompressionInventory = createInventory(largeCompressionResult, 1);

        var decompressionRecipe = recipeManager.getFirstMatch(RecipeType.CRAFTING, decompressionInventory, world);
        if (decompressionRecipe.isEmpty()) return null; // compression can't be decompressed, abort

        var decompressionResult = decompressionRecipe.get().craft(decompressionInventory);
        if (decompressionResult.isEmpty()) return null; // compression can't be decompressed, abort

        if (!decompressed.matches(decompressionResult)) return null;

        return new RecipePair(ItemVariant.of(largeCompressionResult), decompressed, isLarge ? 9 : 4);
    }

    @Nullable
    private RecipePair findDecompressionRecipe(ItemVariant compressed, World world) {
        var stack = compressed.toStack();
        var decompressionInventory = createInventory(stack, 1);

        var decompressionRecipe = recipeManager.getFirstMatch(RecipeType.CRAFTING, decompressionInventory, world);
        if (decompressionRecipe.isEmpty()) return null;

        var decompressionResult = decompressionRecipe.get().craft(decompressionInventory);
        if (decompressionResult.isEmpty()) return null; // Some recipes might match, but not craft

        var largeCompressionInventory = createInventory(decompressionResult, 3);
        var largeCompressionRecipe = recipeManager.getFirstMatch(RecipeType.CRAFTING, largeCompressionInventory, world);
        if (largeCompressionRecipe.isPresent()) {
            var largeCompressionResult = largeCompressionRecipe.get().craft(largeCompressionInventory);
            if (compressed.matches(largeCompressionResult))
                return new RecipePair(compressed, ItemVariant.of(decompressionResult), 9);
        }

        var smallCompressionInventory = createInventory(decompressionResult, 2);
        var smallCompressionRecipe = recipeManager.getFirstMatch(RecipeType.CRAFTING, smallCompressionInventory, world);
        if (smallCompressionRecipe.isPresent()) {
            var smallCompressionResult = smallCompressionRecipe.get().craft(smallCompressionInventory);
            if (compressed.matches(smallCompressionResult))
                return new RecipePair(compressed, ItemVariant.of(decompressionResult), 4);
        }

        return null;
    }

    /**
     * Creates a dummy crafting inventory. Similar to sheep dying so support should be fine
     * @param stack The stack to fill the inventory with
     * @param size The width and height of the inventory. Slot count is size squared.
     * @return A filled crafting inventory of specified size
     */
    private CraftingInventory createInventory(ItemStack stack, int size) {
        var inventory = new CraftingInventory(new ScreenHandler(null, -1) {
            @Override
            public ItemStack transferSlot(PlayerEntity player, int index) {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean canUse(PlayerEntity player) {
                return false;
            }
        }, size, size);
        for (int i = 0; i < size * size; i++) {
            inventory.setStack(i, stack);
        }
        return inventory;
    }

    /**
     * Represents a bidirectional ladder of compression recipes.
     * First step has the base item (e.g. nuggets) and a size of 1.
     * Each step after that has a new item (ingots, blocks) and a size that counts how many of the first tier is necessary (9, 81).
     */
    public record CompressionLadder(List<Step> steps) {
        /**
         * @param item The item at this step.
         * @param size The amount of the first step required to craft this one.
         */
        public record Step(ItemVariant item, int size) {
        }

        public int getPosition(ItemVariant item) {
            for (int i = 0; i < steps.size(); i++) {
                if (steps.get(i).item.equals(item))
                    return i;
            }
            return -1;
        }
    }

    private record RecipePair(ItemVariant compressed, ItemVariant decompressed, int scale) {
    }

    /**
     * Injected into {@link RecipeManager}.
     */
    public interface Provider {
        default CompressionRecipeManager extended_drawers$getCompactingManager() {
            throw new AssertionError("extended_drawers$getCompactingManager must be overridden");
        }
    }
}