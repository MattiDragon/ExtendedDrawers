package io.github.mattidragon.extendeddrawers.extensions.datagen;

import io.github.mattidragon.extendeddrawers.datagen.ReadmeDataProvider;
import io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jspecify.annotations.Nullable;

public class ExtendedDrawersExtensionsDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
        var pack = dataGenerator.createPack();

        pack.addProvider(ExtensionsModelProvider::new);
        pack.addProvider(ExtensionsBlockLootTableProvider::new);
        pack.addProvider(ExtensionsRecipeProvider::new);
        var blockTagProvider = pack.addProvider(ExtensionsBlockTagProvider::new);
        pack.addProvider((output, future) -> new ExtensionsItemTagProvider(output, future, blockTagProvider));
        pack.addProvider(ReadmeDataProvider::new);
    }

    @Override
    public @Nullable String getEffectiveModId() {
        return ExtendedDrawersExtensions.MOD_ID;
    }
}
