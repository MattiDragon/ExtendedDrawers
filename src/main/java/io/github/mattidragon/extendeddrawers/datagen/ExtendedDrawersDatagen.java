package io.github.mattidragon.extendeddrawers.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class ExtendedDrawersDatagen implements DataGeneratorEntrypoint {
    public static final TagKey<Item> DRAWERS = TagKey.of(Registry.ITEM_KEY, id("drawers"));
    
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        dataGenerator.addProvider(DrawersModelProvider::new);
        dataGenerator.addProvider(DrawersBlockLootTableProvider::new);
        dataGenerator.addProvider(DrawersRecipeProvider::new);
        var blockTagProvider = dataGenerator.addProvider(DrawersBlockTagProvider::new);
        dataGenerator.addProvider(new DrawersItemTagProvider(dataGenerator, blockTagProvider));
    }
    
}
