package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;

import java.util.function.Consumer;

class DrawersRecipeProvider extends FabricRecipeProvider {
    public DrawersRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }
    
    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
        offerUpgradeRecipe(exporter, ModItems.DOWNGRADE, Ingredient.ofItems(Items.DIRT), ModItems.UPGRADE_FRAME, Items.STICK);
        offerUpgradeRecipe(exporter, ModItems.T1_UPGRADE, Ingredient.ofItems(Items.BARREL), ModItems.UPGRADE_FRAME, Items.STICK);
        offerUpgradeRecipe(exporter, ModItems.T2_UPGRADE, Ingredient.ofItems(Items.IRON_BLOCK), ModItems.T1_UPGRADE, Items.STICK);
        offerUpgradeRecipe(exporter, ModItems.T3_UPGRADE, Ingredient.ofItems(Items.DIAMOND_BLOCK), ModItems.T2_UPGRADE, Items.BLAZE_ROD);
        offerUpgradeRecipe(exporter, ModItems.T4_UPGRADE, Ingredient.fromTag(ConventionalItemTags.SHULKER_BOXES), ModItems.T3_UPGRADE, Items.END_ROD);
    
        offerDrawerRecipes(exporter);
        offerLockRecipe(exporter);
        offerUpgradeFrameRecipe(exporter);
        offerControllerRecipe(exporter);
    }
    
    private void offerDrawerRecipes(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(ModItems.SHADOW_DRAWER)
                .input('E', Items.END_STONE_BRICKS)
                .input('C', Items.END_CRYSTAL)
                .pattern("EEE")
                .pattern("ECE")
                .pattern("EEE")
                .criterion(RecipeProvider.hasItem(ModItems.SHADOW_DRAWER), RecipeProvider.conditionsFromItem(ModItems.SHADOW_DRAWER))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(ModItems.SINGLE_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("PCP")
                .pattern("LPL")
                .criterion(RecipeProvider.hasItem(ModItems.SINGLE_DRAWER), RecipeProvider.conditionsFromItem(ModItems.SINGLE_DRAWER))
                .offerTo(exporter);
    ShapedRecipeJsonBuilder.create(ModItems.DOUBLE_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("CPC")
                .pattern("LPL")
                .criterion(RecipeProvider.hasItem(ModItems.SINGLE_DRAWER), RecipeProvider.conditionsFromItem(ModItems.SINGLE_DRAWER))
                .offerTo(exporter);
    ShapedRecipeJsonBuilder.create(ModItems.QUAD_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LCL")
                .pattern("CPC")
                .pattern("LCL")
                .criterion(RecipeProvider.hasItem(ModItems.SINGLE_DRAWER), RecipeProvider.conditionsFromItem(ModItems.SINGLE_DRAWER))
                .offerTo(exporter);
    }
    
    private void offerLockRecipe(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(ModItems.LOCK)
                .input('G', Items.GOLD_INGOT)
                .input('g', Items.GOLD_NUGGET)
                .pattern(" g ")
                .pattern("g g")
                .pattern("GGG")
                .criterion(RecipeProvider.hasItem(ModItems.LOCK), RecipeProvider.conditionsFromItem(ModItems.LOCK))
                .offerTo(exporter);
    }
    
    private void offerControllerRecipe(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(ModItems.CONTROLLER)
                .input('I', Items.IRON_INGOT)
                .input('C', Items.COBBLESTONE)
                .input('D', ExtendedDrawersDatagen.DRAWERS)
                .pattern("CIC")
                .pattern("IDI")
                .pattern("CIC")
                .criterion(RecipeProvider.hasItem(ModItems.CONTROLLER), RecipeProvider.conditionsFromItem(ModItems.CONTROLLER))
                .offerTo(exporter);
    }
    
    private void offerUpgradeFrameRecipe(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(ModItems.UPGRADE_FRAME)
                .input('S', Items.STICK)
                .input('C', Items.COBBLESTONE)
                .pattern("SCS")
                .pattern("C C")
                .pattern("SCS")
                .criterion(RecipeProvider.hasItem(ModItems.UPGRADE_FRAME), RecipeProvider.conditionsFromItem(ModItems.UPGRADE_FRAME))
                .offerTo(exporter);
    }
    
    private void offerUpgradeRecipe(Consumer<RecipeJsonProvider> exporter, Item result, Ingredient material, Item base, Item stick) {
        ShapedRecipeJsonBuilder.create(result)
                .input('M', material)
                .input('B', base)
                .input('S', stick)
                .pattern("SSS")
                .pattern("BMB")
                .pattern("SSS")
                .criterion(RecipeProvider.hasItem(result), RecipeProvider.conditionsFromItem(result))
                .offerTo(exporter);
    }
}
