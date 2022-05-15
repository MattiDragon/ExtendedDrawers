package io.github.mattidragon.extendeddrawers.datagen;

import io.github.mattidragon.extendeddrawers.registry.ModItems;
import io.github.mattidragon.extendeddrawers.registry.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import org.jetbrains.annotations.Nullable;

class DrawersItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public DrawersItemTagProvider(FabricDataGenerator dataGenerator, @Nullable BlockTagProvider blockTagProvider) {
        super(dataGenerator, blockTagProvider);
    }
    
    @Override
    protected void generateTags() {
        getOrCreateTagBuilder(ModTags.ItemTags.DRAWERS).add(ModItems.SHADOW_DRAWER, ModItems.SINGLE_DRAWER, ModItems.DOUBLE_DRAWER, ModItems.QUAD_DRAWER);
        getOrCreateTagBuilder(ModTags.ItemTags.UPGRADES).add(ModItems.T1_UPGRADE, ModItems.T2_UPGRADE, ModItems.T3_UPGRADE, ModItems.T4_UPGRADE, ModItems.CREATIVE_UPGRADE, ModItems.DOWNGRADE);
    }
}
