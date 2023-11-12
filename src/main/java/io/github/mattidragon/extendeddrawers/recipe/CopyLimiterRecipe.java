package io.github.mattidragon.extendeddrawers.recipe;

import io.github.mattidragon.extendeddrawers.item.LimiterItem;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModRecipes;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CopyLimiterRecipe extends SpecialCraftingRecipe {
    public CopyLimiterRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        var stacks = inventory.getInputStacks();
        boolean setLimiterFound = false;
        boolean unsetLimiterFound = false;

        for (var stack : stacks) {
            if (stack.isEmpty()) continue;
            if (!stack.isOf(ModItems.LIMITER)) return false;
            var limit = LimiterItem.getLimit(stack);
            if (limit == null) {
                if (!unsetLimiterFound) unsetLimiterFound = true;
                else return false;
            } else {
                if (!setLimiterFound) setLimiterFound = true;
                else return false;
            }
        }

        return setLimiterFound && unsetLimiterFound;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        var stacks = inventory.getInputStacks();
        Long limit = null;

        for (var stack : stacks) {
            if (stack.isEmpty()) continue;
            var checkingLimit = LimiterItem.getLimit(stack);
            if (checkingLimit != null)  {
                limit = checkingLimit;
            }
        }

        if (limit == null) // something went wrong
            return ItemStack.EMPTY;

        var stack = ModItems.LIMITER.getDefaultStack();
        stack.getOrCreateNbt().putLong("limit", limit);
        return stack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(RecipeInputInventory inventory) {
        var result = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);

        for(int i = 0; i < result.size(); ++i) {
            var stack = inventory.getStack(i);
            var item = stack.getItem();
            if (item.getRecipeRemainder() != null) {
                result.set(i, stack.getRecipeRemainder());
            } else if (item instanceof LimiterItem && LimiterItem.getLimit(stack) != null) {
                result.set(i, stack.copyWithCount(1));
            }
        }

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COPY_LIMITER_SERIALIZER;
    }
}
