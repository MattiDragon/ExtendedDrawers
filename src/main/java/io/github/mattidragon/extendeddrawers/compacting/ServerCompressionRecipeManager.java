package io.github.mattidragon.extendeddrawers.compacting;

import io.github.mattidragon.extendeddrawers.misc.RecipeManagerAccess;
import io.github.mattidragon.extendeddrawers.networking.CompressionRecipeSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class ServerCompressionRecipeManager implements CompressionRecipeManager {
    private final RecipeManager recipeManager;
    private final Map<ItemVariant, CompressionLadder> ladders = new HashMap<>();
    private final List<CompressionLadder> overrides = new ArrayList<>();

    public ServerCompressionRecipeManager(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    public static ServerCompressionRecipeManager of(RecipeManager recipeManager) {
        return ((io.github.mattidragon.extendeddrawers.compacting.ServerCompressionRecipeManager.Provider) recipeManager).extended_drawers$getCompactingManager();
    }

    public void setOverrides(List<CompressionLadder> overrides) {
        this.overrides.clear();
        this.overrides.addAll(overrides);
        reload();
    }

    public Collection<CompressionLadder> getLadders() {
        return ladders.values();
    }

    public void reload() {
        ladders.clear();
        for (var override : overrides) {
            addLadder(override);
        }
    }

    @Override
    public CompressionLadder getLadder(ItemVariant item, Level level) {
        if (ladders.containsKey(item))
            return ladders.get(item);
        var ladder = buildLadder(item, level);
        // Put ladder in map for all items
        addLadder(ladder);
        if (level instanceof ServerLevel serverLevel) {
            for (var player : serverLevel.players()) {
                ServerPlayNetworking.send(player, new CompressionRecipeSyncPayload(List.of(ladder), false));
            }
        }
        return ladder;
    }

    private void addLadder(CompressionLadder ladder) {
        ladder.steps().forEach(step -> ladders.put(step.item(), ladder));
    }

    private CompressionLadder buildLadder(ItemVariant item, Level level) {
        var bottom = findBottom(item, level);
        var ladder = new ArrayList<CompressionLadder.Step>();
        var visited = new HashSet<ItemVariant>();
        var currentItem = bottom;
        var currentSize = 1;
        visited.add(currentItem);
        ladder.add(new CompressionLadder.Step(currentItem, currentSize));

        while (true) {
            var pair = findCompressionRecipe(currentItem, level);
            if (pair == null) break; // Reached top of ladder
            currentItem = pair.compressed;
            currentSize *= pair.scale;
            if (!visited.add(currentItem)) break; // Ladder is cyclic, all items accounted for
            ladder.add(new CompressionLadder.Step(currentItem, currentSize));
        }
        return new CompressionLadder(List.copyOf(ladder));
    }

    private ItemVariant findBottom(ItemVariant item, Level level) {
        var visited = new HashSet<ItemVariant>();
        var candidate = item;
        while (true) {
            var pair = findDecompressionRecipe(candidate, level);
            if (pair == null) break; // Reached bottom
            if (visited.contains(pair.decompressed)) break; // Next item would be a cycle, this is the bottom now
            candidate = pair.decompressed;
            visited.add(candidate);
        }
        return candidate;
    }

    @Nullable
    private RecipePair findCompressionRecipe(ItemVariant decompressed, Level level) {
        return IntStream.of(3, 2, 1)
                .mapToObj(size -> findCompressionRecipeForSize(decompressed, level, size))
                .flatMap(Function.identity())
                .findFirst()
                .orElse(null);
    }

    private Stream<RecipePair> findCompressionRecipeForSize(ItemVariant decompressed, Level level, int size) {
        var decompressedStack = decompressed.toStack(size * size);
        return findRecipes(decompressed.toStack(), size, level) // Find compression recipes
                .filter(compressed -> findRecipes(compressed, 1, level).anyMatch(decompressed2 -> ItemStack.matches(decompressed2, decompressedStack))) // Find matching decompression recipe
                .map(compressed -> new RecipePair(ItemVariant.of(compressed), decompressed, size * size));
    }

    @Nullable
    private RecipePair findDecompressionRecipe(ItemVariant compressed, Level level) {
        var compressedStack = compressed.toStack();
        return findRecipes(compressedStack, 1, level) // Find decompression recipe
                .flatMap(decompressed -> IntStream.of(3, 2, 1) // Check each size from largest to smallest for matching compression recipes
                        .filter(size -> findRecipes(decompressed, size, level).anyMatch(compressed2 -> ItemStack.matches(compressedStack, compressed2)))
                        .mapToObj(size -> new RecipePair(compressed, ItemVariant.of(decompressed), size * size)))
                .findFirst()
                .orElse(null);
    }

    private Stream<ItemStack> findRecipes(ItemStack stack, int size, Level level) {
        var inventory = createInventory(stack, size);
        return ((RecipeManagerAccess) recipeManager).getRecipes().getRecipesFor(RecipeType.CRAFTING, inventory, level)
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.getRemainingItems(inventory).stream().allMatch(ItemStack::isEmpty)) // We can't deal with remainders, so we just prevent recipe with them from being used
                .map(recipe -> recipe.assemble(inventory))
                .filter(result -> !result.isEmpty());
    }

    /**
     * Creates a dummy crafting inventory. Similar to sheep dying so support should be fine
     * @param stack The stack to fill the inventory with
     * @param size The width and height of the inventory. Slot count is size squared.
     * @return A filled crafting inventory of specified size
     */
    private CraftingInput createInventory(ItemStack stack, int size) {
        var list = new ArrayList<ItemStack>(size * size);
        for (int i = 0; i < size * size; i++) {
            list.add(stack);
        }
        return CraftingInput.of(size, size, list);
    }

    private record RecipePair(ItemVariant compressed, ItemVariant decompressed, int scale) {
    }
    
    public interface Provider extends CompressionRecipeManager.Provider {
        ServerCompressionRecipeManager extended_drawers$getCompactingManager();
    }
}
