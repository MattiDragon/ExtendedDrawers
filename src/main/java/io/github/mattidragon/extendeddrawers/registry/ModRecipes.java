package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.recipe.CopyLimiterRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
    public static final RecipeSerializer<CopyLimiterRecipe> COPY_LIMITER_SERIALIZER = new SpecialCraftingRecipe.SpecialRecipeSerializer<>(CopyLimiterRecipe::new);

    private ModRecipes() {}

    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, ExtendedDrawers.id("copy_limiter"), COPY_LIMITER_SERIALIZER);
    }
}
