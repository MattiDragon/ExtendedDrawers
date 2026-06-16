package io.github.mattidragon.extendeddrawers.recipe;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModRecipes {
    private ModRecipes() {}

    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ExtendedDrawers.id("copy_limiter"), CopyLimiterRecipe.SERIALIZER);
    }
}
