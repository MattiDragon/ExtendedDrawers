package io.github.mattidragon.extendeddrawers.registry;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.recipe.CopyLimiterRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipes {
    public static final RecipeSerializer<CopyLimiterRecipe> COPY_LIMITER_SERIALIZER = new CustomRecipe.Serializer<>(CopyLimiterRecipe::new);

    private ModRecipes() {}

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ExtendedDrawers.id("copy_limiter"), COPY_LIMITER_SERIALIZER);
    }
}
