package io.github.mattidragon.extendeddrawers.recipe;

import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import io.github.mattidragon.extendeddrawers.item.LimiterItem;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CopyLimiterRecipe extends CustomRecipe {
    public CopyLimiterRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level world) {
        var stacks = input.items();
        boolean setLimiterFound = false;
        boolean unsetLimiterFound = false;

        for (var stack : stacks) {
            if (stack.isEmpty()) continue;
            if (!stack.is(ModItems.LIMITER)) return false;
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        var stacks = input.items();
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

        var stack = ModItems.LIMITER.getDefaultInstance();
        stack.set(ModDataComponents.LIMITER_LIMIT, new LimiterLimitComponent(limit));
        return stack;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        var result = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for(int i = 0; i < result.size(); ++i) {
            var stack = input.getItem(i);
            var item = stack.getItem();
            if (item.getCraftingRemainder() != null) {
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
