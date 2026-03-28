package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.recipe.CopyLimiterRecipe;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

class DrawersRecipeGenerator extends RecipeProvider {
    public DrawersRecipeGenerator(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    public void buildRecipes() {
        offerUpgradeRecipe(output, ModItems.T1_UPGRADE, Ingredient.of(Items.BARREL), ModItems.UPGRADE_FRAME, Items.STICK);
        offerUpgradeRecipe(output, ModItems.T2_UPGRADE, Ingredient.of(Items.IRON_BLOCK), ModItems.T1_UPGRADE, Items.STICK);
        offerUpgradeRecipe(output, ModItems.T3_UPGRADE, Ingredient.of(Items.DIAMOND_BLOCK), ModItems.T2_UPGRADE, Items.BLAZE_ROD);
        offerUpgradeRecipe(output, ModItems.T4_UPGRADE, tag(ConventionalItemTags.SHULKER_BOXES), ModItems.T3_UPGRADE, Items.END_ROD);

        offerDrawerRecipes(output);
        offerLockRecipe(output);
        offerLimiterRecipe(output);
        offerUpgradeFrameRecipe(output);
        offerAccessPointRecipe(output);
        offerConnectorRecipe(output);

        SpecialRecipeBuilder.special(CopyLimiterRecipe::new)
                .save(output, ResourceKey.create(Registries.RECIPE, ExtendedDrawers.id("copy_limiter")));
    }

    private void offerDrawerRecipes(RecipeOutput exporter) {
        shaped(RecipeCategory.DECORATIONS, ModItems.SHADOW_DRAWER)
                .define('E', Items.END_STONE_BRICKS)
                .define('C', Items.END_CRYSTAL)
                .pattern("EEE")
                .pattern("ECE")
                .pattern("EEE")
                .unlockedBy(getHasName(Items.END_STONE_BRICKS), has(Items.END_STONE_BRICKS))
                .save(exporter);
        shaped(RecipeCategory.DECORATIONS, ModItems.COMPACTING_DRAWER)
                .define('C', Items.CHEST)
                .define('S', ItemTags.STONE_CRAFTING_MATERIALS)
                .define('I', Items.IRON_BLOCK)
                .define('P', Items.PISTON)
                .pattern("SPS")
                .pattern("CIC")
                .pattern("SPS")
                .unlockedBy(getHasName(Items.CHEST), has(Items.CHEST))
                .save(exporter);
        shaped(RecipeCategory.DECORATIONS, ModItems.SINGLE_DRAWER)
                .define('C', Items.CHEST)
                .define('L', ItemTags.LOGS)
                .define('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("PCP")
                .pattern("LPL")
                .unlockedBy(getHasName(Items.CHEST), has(Items.CHEST))
                .save(exporter);
        shaped(RecipeCategory.DECORATIONS, ModItems.DOUBLE_DRAWER)
                .define('C', Items.CHEST)
                .define('L', ItemTags.LOGS)
                .define('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("CPC")
                .pattern("LPL")
                .unlockedBy(getHasName(Items.CHEST), has(Items.CHEST))
                .save(exporter);
        shaped(RecipeCategory.DECORATIONS, ModItems.QUAD_DRAWER)
                .define('C', Items.CHEST)
                .define('L', ItemTags.LOGS)
                .define('P', ItemTags.PLANKS)
                .pattern("LCL")
                .pattern("CPC")
                .pattern("LCL")
                .unlockedBy(getHasName(Items.CHEST), has(Items.CHEST))
                .save(exporter);
    }

    private void offerLockRecipe(RecipeOutput exporter) {
        shaped(RecipeCategory.TOOLS, ModItems.LOCK)
                .define('G', Items.GOLD_INGOT)
                .define('g', Items.GOLD_NUGGET)
                .pattern(" g ")
                .pattern("g g")
                .pattern("GGG")
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(exporter);
    }

    private void offerLimiterRecipe(RecipeOutput exporter) {
        shaped(RecipeCategory.REDSTONE, ModItems.LIMITER)
                .define('C', Items.COPPER_INGOT)
                .define('R', Items.REDSTONE)
                .define('E', Items.ENDER_PEARL)
                .pattern("RCR")
                .pattern("CEC")
                .pattern("RCR")
                .unlockedBy("has_drawer", has(ModTags.ItemTags.DRAWERS))
                .save(exporter);
    }

    private void offerAccessPointRecipe(RecipeOutput exporter) {
        shaped(RecipeCategory.DECORATIONS, ModItems.ACCESS_POINT)
                .define('I', Items.IRON_INGOT)
                .define('C', Items.COBBLESTONE)
                .define('D', ModTags.ItemTags.DRAWERS)
                .pattern("CIC")
                .pattern("IDI")
                .pattern("CIC")
                .unlockedBy("has_drawer", has(ModTags.ItemTags.DRAWERS))
                .save(exporter);
    }

    private void offerConnectorRecipe(RecipeOutput exporter) {
        shaped(RecipeCategory.DECORATIONS, ModItems.CONNECTOR, 8)
                .define('L', ItemTags.LOGS)
                .define('P', ItemTags.PLANKS)
                .pattern("LPL")
                .pattern("PPP")
                .pattern("LPL")
                .unlockedBy("has_drawer", has(ModTags.ItemTags.DRAWERS))
                .save(exporter);
    }

    private void offerUpgradeFrameRecipe(RecipeOutput exporter) {
        shaped(RecipeCategory.MISC, ModItems.UPGRADE_FRAME)
                .define('S', Items.STICK)
                .define('C', Items.COBBLESTONE)
                .pattern("SCS")
                .pattern("C C")
                .pattern("SCS")
                .unlockedBy("has_drawer", has(ModTags.ItemTags.DRAWERS))
                .save(exporter);
    }

    private void offerUpgradeRecipe(RecipeOutput exporter, Item result, Ingredient material, Item base, Item stick) {
        shaped(RecipeCategory.MISC, result)
                .define('M', material)
                .define('B', base)
                .define('S', stick)
                .pattern("SSS")
                .pattern("BMB")
                .pattern("SSS")
                .unlockedBy(getHasName(ModItems.UPGRADE_FRAME), has(ModItems.UPGRADE_FRAME))
                .save(exporter);
    }
}
