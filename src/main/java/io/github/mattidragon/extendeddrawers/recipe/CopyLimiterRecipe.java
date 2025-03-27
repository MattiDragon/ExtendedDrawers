package io.github.mattidragon.extendeddrawers.recipe;

import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import io.github.mattidragon.extendeddrawers.item.LimiterItem;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CopyLimiterRecipe extends SpecialCraftingRecipe {
    public CopyLimiterRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        var stacks = input.getStacks();
        boolean setLimiterFound = false;
        boolean unsetLimiterFound = false;

        for (var stack : stacks) {
            if (stack.isEmpty()) continue;
            if (!stack.isOf(ModItems.LIMITER)) return false;
            var limit = stack.get(ModDataComponents.LIMITER_LIMIT);
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
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registryLookup) {
        var stacks = input.getStacks();
        Long limit = null;

        for (var stack : stacks) {
            if (stack.isEmpty()) continue;
            var checkingLimit = stack.get(ModDataComponents.LIMITER_LIMIT);
            if (checkingLimit != null)  {
                limit = checkingLimit.limit();
            }
        }

        if (limit == null) // something went wrong
            return ItemStack.EMPTY;

        var stack = ModItems.LIMITER.getDefaultStack();
        stack.set(ModDataComponents.LIMITER_LIMIT, new LimiterLimitComponent(limit));
        return stack;
    }

    @Override
    public DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input) {
        var result = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

        for(int i = 0; i < result.size(); ++i) {
            var stack = input.getStackInSlot(i);
            var item = stack.getItem();
            if (item.getRecipeRemainder() != null) {
                result.set(i, stack.getRecipeRemainder());
            } else {
                if (item instanceof LimiterItem && stack.get(ModDataComponents.LIMITER_LIMIT) != null) {
                    result.set(i, stack.copyWithCount(1));
                }
            }
        }

        return result;
    }

    @Override
    public RecipeSerializer<CopyLimiterRecipe> getSerializer() {
        return ModRecipes.COPY_LIMITER_SERIALIZER;
    }
}
