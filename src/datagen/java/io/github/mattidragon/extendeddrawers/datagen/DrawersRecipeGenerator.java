package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.recipe.CopyLimiterRecipe;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.data.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

class DrawersRecipeGenerator extends RecipeGenerator {
    public DrawersRecipeGenerator(RegistryWrapper.WrapperLookup registries, RecipeExporter exporter) {
        super(registries, exporter);
    }

    @Override
    public void generate() {
        offerUpgradeRecipe(exporter, ModItems.T1_UPGRADE, Ingredient.ofItems(Items.BARREL), ModItems.UPGRADE_FRAME, Items.STICK);
        offerUpgradeRecipe(exporter, ModItems.T2_UPGRADE, Ingredient.ofItems(Items.IRON_BLOCK), ModItems.T1_UPGRADE, Items.STICK);
        offerUpgradeRecipe(exporter, ModItems.T3_UPGRADE, Ingredient.ofItems(Items.DIAMOND_BLOCK), ModItems.T2_UPGRADE, Items.BLAZE_ROD);
        offerUpgradeRecipe(exporter, ModItems.T4_UPGRADE, ingredientFromTag(ConventionalItemTags.SHULKER_BOXES), ModItems.T3_UPGRADE, Items.END_ROD);

        offerDrawerRecipes(exporter);
        offerLockRecipe(exporter);
        offerLimiterRecipe(exporter);
        offerUpgradeFrameRecipe(exporter);
        offerAccessPointRecipe(exporter);
        offerConnectorRecipe(exporter);

        ComplexRecipeJsonBuilder.create(CopyLimiterRecipe::new)
                .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, ExtendedDrawers.id("copy_limiter")));
    }

    private void offerDrawerRecipes(RecipeExporter exporter) {
        createShaped(RecipeCategory.DECORATIONS, ModItems.SHADOW_DRAWER)
                .input('E', Items.END_STONE_BRICKS)
                .input('C', Items.END_CRYSTAL)
                .pattern("EEE")
                .pattern("ECE")
                .pattern("EEE")
                .criterion(hasItem(Items.END_STONE_BRICKS), conditionsFromItem(Items.END_STONE_BRICKS))
                .offerTo(exporter);
        createShaped(RecipeCategory.DECORATIONS, ModItems.COMPACTING_DRAWER)
                .input('C', Items.CHEST)
                .input('S', ItemTags.STONE_CRAFTING_MATERIALS)
                .input('I', Items.IRON_BLOCK)
                .input('P', Items.PISTON)
                .pattern("SPS")
                .pattern("CIC")
                .pattern("SPS")
                .criterion(hasItem(Items.CHEST), conditionsFromItem(Items.CHEST))
                .offerTo(exporter);
        createShaped(RecipeCategory.DECORATIONS, ModItems.SINGLE_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("PCP")
                .pattern("LPL")
                .criterion(hasItem(Items.CHEST), conditionsFromItem(Items.CHEST))
                .offerTo(exporter);
        createShaped(RecipeCategory.DECORATIONS, ModItems.DOUBLE_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("CPC")
                .pattern("LPL")
                .criterion(hasItem(Items.CHEST), conditionsFromItem(Items.CHEST))
                .offerTo(exporter);
        createShaped(RecipeCategory.DECORATIONS, ModItems.QUAD_DRAWER)
                .input('C', Items.CHEST)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LCL")
                .pattern("CPC")
                .pattern("LCL")
                .criterion(hasItem(Items.CHEST), conditionsFromItem(Items.CHEST))
                .offerTo(exporter);
    }

    private void offerLockRecipe(RecipeExporter exporter) {
        createShaped(RecipeCategory.TOOLS, ModItems.LOCK)
                .input('G', Items.GOLD_INGOT)
                .input('g', Items.GOLD_NUGGET)
                .pattern(" g ")
                .pattern("g g")
                .pattern("GGG")
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter);
    }

    private void offerLimiterRecipe(RecipeExporter exporter) {
        createShaped(RecipeCategory.REDSTONE, ModItems.LIMITER)
                .input('C', Items.COPPER_INGOT)
                .input('R', Items.REDSTONE)
                .input('E', Items.ENDER_PEARL)
                .pattern("RCR")
                .pattern("CEC")
                .pattern("RCR")
                .criterion("has_drawer", conditionsFromTag(ModTags.ItemTags.DRAWERS))
                .offerTo(exporter);
    }

    private void offerAccessPointRecipe(RecipeExporter exporter) {
        createShaped(RecipeCategory.DECORATIONS, ModItems.ACCESS_POINT)
                .input('I', Items.IRON_INGOT)
                .input('C', Items.COBBLESTONE)
                .input('D', ModTags.ItemTags.DRAWERS)
                .pattern("CIC")
                .pattern("IDI")
                .pattern("CIC")
                .criterion("has_drawer", conditionsFromTag(ModTags.ItemTags.DRAWERS))
                .offerTo(exporter);
    }

    private void offerConnectorRecipe(RecipeExporter exporter) {
        createShaped(RecipeCategory.DECORATIONS, ModItems.CONNECTOR, 8)
                .input('L', ItemTags.LOGS)
                .input('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("PPP")
                .pattern("LPL")
                .criterion("has_drawer", conditionsFromTag(ModTags.ItemTags.DRAWERS))
                .offerTo(exporter);
    }

    private void offerUpgradeFrameRecipe(RecipeExporter exporter) {
        createShaped(RecipeCategory.MISC, ModItems.UPGRADE_FRAME)
                .input('S', Items.STICK)
                .input('C', Items.COBBLESTONE)
                .pattern("SCS")
                .pattern("C C")
                .pattern("SCS")
                .criterion("has_drawer", conditionsFromTag(ModTags.ItemTags.DRAWERS))
                .offerTo(exporter);
    }

    private void offerUpgradeRecipe(RecipeExporter exporter, Item result, Ingredient material, Item base, Item stick) {
        createShaped(RecipeCategory.MISC, result)
                .input('M', material)
                .input('B', base)
                .input('S', stick)
                .pattern("SSS")
                .pattern("BMB")
                .pattern("SSS")
                .criterion(hasItem(ModItems.UPGRADE_FRAME), conditionsFromItem(ModItems.UPGRADE_FRAME))
                .offerTo(exporter);
    }
}
