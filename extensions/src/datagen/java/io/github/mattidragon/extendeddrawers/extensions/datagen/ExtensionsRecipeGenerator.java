package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

class ExtensionsRecipeGenerator extends RecipeProvider {
    public ExtensionsRecipeGenerator(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    public void buildRecipes() {
        offerBarrelDrawerRecipes(output);
        offerEnderConnectorRecipe(output);
        offerLinkerRecipe(output);
    }

    private void offerBarrelDrawerRecipes(RecipeOutput exporter) {
        shaped(RecipeCategory.DECORATIONS, ExtensionItems.DRAWER_BARREL)
                .define('B', Items.BARREL)
                .define('L', ItemTags.LOGS)
                .define('R', Items.RESIN_BRICK)
                .pattern("LRL")
                .pattern("RBR")
                .pattern("LRL")
                .unlockedBy(getHasName(Items.BARREL), has(Items.BARREL))
                .save(exporter, "drawer_barrel_from_resin");
        shaped(RecipeCategory.DECORATIONS, ExtensionItems.DRAWER_BARREL)
                .define('B', Items.BARREL)
                .define('L', ItemTags.LOGS)
                .define('R', Items.NETHERITE_SCRAP)
                .pattern("LRL")
                .pattern("RBR")
                .pattern("LRL")
                .unlockedBy(getHasName(Items.BARREL), has(Items.BARREL))
                .save(exporter, "drawer_barrel_from_scrap");
    }

    private void offerEnderConnectorRecipe(RecipeOutput exporter) {
        shaped(RecipeCategory.DECORATIONS, ExtensionItems.ENDER_CONNECTOR, 2)
                .define('P', Items.ENDER_PEARL)
                .define('C', Items.END_CRYSTAL)
                .define('E', Items.END_STONE_BRICKS)
                .pattern("PEP")
                .pattern("ECE")
                .pattern("PEP")
                .unlockedBy(getHasName(Items.END_STONE_BRICKS), has(Items.END_STONE_BRICKS))
                .save(exporter);
    }

    private void offerLinkerRecipe(RecipeOutput exporter) {
        shaped(RecipeCategory.TOOLS, ExtensionItems.ENDER_CONNECTOR_LINKER)
                .define('|', Items.BLAZE_ROD)
                .define('E', Items.ENDER_EYE)
                .pattern("E")
                .pattern("|")
                .pattern("|")
                .unlockedBy(getHasName(Items.END_STONE_BRICKS), has(Items.END_STONE_BRICKS))
                .save(exporter);
    }
}
