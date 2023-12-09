package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.recipe.CopyLimiterRecipe;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModRecipes;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

class DrawersRecipeProvider extends FabricRecipeProvider {
    public DrawersRecipeProvider(FabricDataOutput output) {
        super(output);
    }
    
    @Override
    public void generate(RecipeExporter exporter) {
        offerUpgradeRecipe(exporter, ModItems.T1_UPGRADE, Ingredient.ofItems(Items.BARREL), ModItems.UPGRADE_FRAME, Items.STICK);
        offerUpgradeRecipe(exporter, ModItems.T2_UPGRADE, Ingredient.ofItems(Items.IRON_BLOCK), ModItems.T1_UPGRADE, Items.STICK);
        offerUpgradeRecipe(exporter, ModItems.T3_UPGRADE, Ingredient.ofItems(Items.DIAMOND_BLOCK), ModItems.T2_UPGRADE, Items.BLAZE_ROD);
        offerUpgradeRecipe(exporter, ModItems.T4_UPGRADE, Ingredient.fromTag(ConventionalItemTags.SHULKER_BOXES), ModItems.T3_UPGRADE, Items.END_ROD);
    
        offerDrawerRecipes(exporter);
        offerLockRecipe(exporter);
        offerLimiterRecipe(exporter);
        offerUpgradeFrameRecipe(exporter);
        offerAccessPointRecipe(exporter);
        offerConnectorRecipe(exporter);

        ComplexRecipeJsonBuilder.create(CopyLimiterRecipe::new)
                .offerTo(exporter, ExtendedDrawers.id("copy_limiter"));
    }
    
    private void offerDrawerRecipes(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModItems.SHADOW_DRAWER)
                .input('E', Items.END_STONE_BRICKS)
                .input('C', Items.END_CRYSTAL)
                .pattern("EEE")
                .pattern("ECE")
                .pattern("EEE")
                .criterion(RecipeProvider.hasItem(Items.END_STONE_BRICKS), RecipeProvider.conditionsFromItem(Items.END_STONE_BRICKS))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModItems.COMPACTING_DRAWER)
                .input('C', Items.CHEST)
                .input('S', ItemTags.STONE_CRAFTING_MATERIALS)
                .input('I', Items.IRON_BLOCK)
                .input('P', Items.PISTON)
                .pattern("SPS")
                .pattern("CIC")
                .pattern("SPS")
                .criterion(RecipeProvider.hasItem(Items.CHEST), RecipeProvider.conditionsFromItem(Items.CHEST))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModItems.SINGLE_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("PCP")
                .pattern("LPL")
                .criterion(RecipeProvider.hasItem(Items.CHEST), RecipeProvider.conditionsFromItem(Items.CHEST))
                .offerTo(exporter);
    ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModItems.DOUBLE_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("CPC")
                .pattern("LPL")
                .criterion(RecipeProvider.hasItem(Items.CHEST), RecipeProvider.conditionsFromItem(Items.CHEST))
                .offerTo(exporter);
    ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModItems.QUAD_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LCL")
                .pattern("CPC")
                .pattern("LCL")
                .criterion(RecipeProvider.hasItem(Items.CHEST), RecipeProvider.conditionsFromItem(Items.CHEST))
                .offerTo(exporter);
    }
    
    private void offerLockRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.LOCK)
                .input('G', Items.GOLD_INGOT)
                .input('g', Items.GOLD_NUGGET)
                .pattern(" g ")
                .pattern("g g")
                .pattern("GGG")
                .criterion(RecipeProvider.hasItem(Items.GOLD_INGOT), RecipeProvider.conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter);
    }

    private void offerLimiterRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModItems.LIMITER)
                .input('C', Items.COPPER_INGOT)
                .input('R', Items.REDSTONE)
                .input('E', Items.ENDER_PEARL)
                .pattern("RCR")
                .pattern("CEC")
                .pattern("RCR")
                .criterion("has_drawer", RecipeProvider.conditionsFromTag(ModTags.ItemTags.DRAWERS))
                .offerTo(exporter);
    }
    
    private void offerAccessPointRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModItems.ACCESS_POINT)
                .input('I', Items.IRON_INGOT)
                .input('C', Items.COBBLESTONE)
                .input('D', ModTags.ItemTags.DRAWERS)
                .pattern("CIC")
                .pattern("IDI")
                .pattern("CIC")
                .criterion("has_drawer", RecipeProvider.conditionsFromTag(ModTags.ItemTags.DRAWERS))
                .offerTo(exporter);
    }
    
    private void offerConnectorRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModItems.CONNECTOR)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("PPP")
                .pattern("LPL")
                .criterion("has_drawer", RecipeProvider.conditionsFromTag(ModTags.ItemTags.DRAWERS))
                .offerTo(exporter);
    }
    
    private void offerUpgradeFrameRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.UPGRADE_FRAME)
                .input('S', Items.STICK)
                .input('C', Items.COBBLESTONE)
                .pattern("SCS")
                .pattern("C C")
                .pattern("SCS")
                .criterion("has_drawer", RecipeProvider.conditionsFromTag(ModTags.ItemTags.DRAWERS))
                .offerTo(exporter);
    }
    
    private void offerUpgradeRecipe(RecipeExporter exporter, Item result, Ingredient material, Item base, Item stick) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, result)
                .input('M', material)
                .input('B', base)
                .input('S', stick)
                .pattern("SSS")
                .pattern("BMB")
                .pattern("SSS")
                .criterion(RecipeProvider.hasItem(ModItems.UPGRADE_FRAME), RecipeProvider.conditionsFromItem(ModItems.UPGRADE_FRAME))
                .offerTo(exporter);
    }
}
