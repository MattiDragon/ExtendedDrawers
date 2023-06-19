package io.github.mattidragon.extendeddrawers.compacting;

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
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public final class CompressionRecipeManager {
    private final RecipeManager recipeManager;
    private final Map<ItemVariant, CompressionLadder> ladders = new HashMap<>();
    private final List<CompressionLadder> overrides = new ArrayList<>();

    public CompressionRecipeManager(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public static CompressionRecipeManager of(RecipeManager recipeManager) {
        return ((Provider) recipeManager).extended_drawers$getCompactingManager();
    }

    public void setOverrides(List<CompressionLadder> overrides) {
        this.overrides.clear();
        this.overrides.addAll(overrides);
        reload();
    }

    public List<CompressionLadder> getOverrides() {
        return overrides;
    }

    public void reload() {
        ladders.clear();
        for (var override : overrides) {
            addLadder(override);
        }
    }

    public CompressionLadder getLadder(ItemVariant item, World world) {
        if (ladders.containsKey(item))
            return ladders.get(item);
        var ladder = buildLadder(item, world);
        // Put ladder in map for all items
        addLadder(ladder);
        return ladder;
    }

    private void addLadder(CompressionLadder ladder) {
        ladder.steps().forEach(step -> ladders.put(step.item(), ladder));
    }

    private CompressionLadder buildLadder(ItemVariant item, World world) {
        var bottom = findBottom(item, world);
        var ladder = new ArrayList<CompressionLadder.Step>();
        var visited = new HashSet<ItemVariant>();
        var currentItem = bottom;
        var currentSize = 1;
        visited.add(currentItem);
        ladder.add(new CompressionLadder.Step(currentItem, currentSize));

        while (true) {
            var pair = findCompressionRecipe(currentItem, world);
            if (pair == null) break; // Reached top of ladder
            currentItem = pair.compressed;
            currentSize *= pair.scale;
            if (!visited.add(currentItem)) break; // Ladder is cyclic, all items accounted for
            ladder.add(new CompressionLadder.Step(currentItem, currentSize));
        }
        return new CompressionLadder(List.copyOf(ladder));
    }

    private ItemVariant findBottom(ItemVariant item, World world) {
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
        return IntStream.of(3, 2, 1)
                .mapToObj(size -> findCompressionRecipeForSize(decompressed, world, size))
                .flatMap(Function.identity())
                .findFirst()
                .orElse(null);
    }

    private Stream<RecipePair> findCompressionRecipeForSize(ItemVariant decompressed, World world, int size) {
        return findRecipes(decompressed.toStack(), size, world) // Find compression recipes
                .filter(compressed -> findRecipes(compressed, 1, world).anyMatch(decompressed::matches))// Find matching decompression recipe
                .map(compressed -> new RecipePair(ItemVariant.of(compressed), decompressed, size));
    }

    @Nullable
    private RecipePair findDecompressionRecipe(ItemVariant compressed, World world) {
        return findRecipes(compressed.toStack(), 1, world) // Find decompression recipe
                .flatMap(result -> IntStream.of(3, 2, 1) // Check each size from largest to smallest for matching compression recipes
                        .filter(size -> findRecipes(result, size, world).anyMatch(compressed::matches))
                        .mapToObj(size -> new RecipePair(compressed, ItemVariant.of(result), size * size)))
                .findFirst()
                .orElse(null);
    }

    private Stream<ItemStack> findRecipes(ItemStack stack, int size, World world) {
        var inventory = createInventory(stack, size);
        return recipeManager.getAllMatches(RecipeType.CRAFTING, inventory, world)
                .stream()
                .filter(recipe -> recipe.getRemainder(inventory).stream().allMatch(ItemStack::isEmpty)) // We can't deal with remainders, so we just prevent recipe with them from being used
                .map(recipe -> recipe.craft(inventory, world.getRegistryManager()))
                .filter(result -> !result.isEmpty());
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
            public ItemStack quickMove(PlayerEntity player, int slot) {
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
