package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.extensions.registry.ExtensionItems;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

class ExtensionsRecipeGenerator extends RecipeGenerator {
    public ExtensionsRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        super(registries, exporter);
    }

    @Override
    public void generate() {
        offerBarrelDrawerRecipes(exporter);
        offerEnderConnectorRecipe(exporter);
        offerLinkerRecipe(exporter);
    }

    private void offerBarrelDrawerRecipes(RecipeExporter exporter) {
        createShaped(RecipeCategory.DECORATIONS, ExtensionItems.DRAWER_BARREL)
                .input('B', Items.BARREL)
                .input('L', ItemTags.LOGS)
                .input('R', Items.RESIN_BRICK)
                .pattern("LRL")
                .pattern("RBR")
                .pattern("LRL")
                .criterion(hasItem(Items.BARREL), conditionsFromItem(Items.BARREL))
                .offerTo(exporter, "drawer_barrel_from_resin");
        createShaped(RecipeCategory.DECORATIONS, ExtensionItems.DRAWER_BARREL)
                .input('B', Items.BARREL)
                .input('L', ItemTags.LOGS)
                .input('R', Items.NETHERITE_SCRAP)
                .pattern("LRL")
                .pattern("RBR")
                .pattern("LRL")
                .criterion(hasItem(Items.BARREL), conditionsFromItem(Items.BARREL))
                .offerTo(exporter, "drawer_barrel_from_scrap");
    }

    private void offerEnderConnectorRecipe(RecipeExporter exporter) {
        createShaped(RecipeCategory.DECORATIONS, ExtensionItems.ENDER_CONNECTOR, 2)
                .input('P', Items.ENDER_PEARL)
                .input('C', Items.END_CRYSTAL)
                .input('E', Items.END_STONE_BRICKS)
                .pattern("PEP")
                .pattern("ECE")
                .pattern("PEP")
                .criterion(hasItem(Items.END_STONE_BRICKS), conditionsFromItem(Items.END_STONE_BRICKS))
                .offerTo(exporter);
    }

    private void offerLinkerRecipe(RecipeExporter exporter) {
        createShaped(RecipeCategory.TOOLS, ExtensionItems.ENDER_CONNECTOR_LINKER)
                .input('|', Items.BLAZE_ROD)
                .input('E', Items.ENDER_EYE)
                .pattern("E")
                .pattern("|")
                .pattern("|")
                .criterion(hasItem(Items.END_STONE_BRICKS), conditionsFromItem(Items.END_STONE_BRICKS))
                .offerTo(exporter);
    }
}
