package io.github.mattidragon.extendeddrawers.compacting;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import net.fabricmc.fabric.api.resource.v1.DataResourceLoader;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

public class ModResourceReloaders {
    private static final Identifier COMPRESSION_OVERRIDES_ID = ExtendedDrawers.id("compression_overrides");
    private static final Identifier COMPRESSION_RECIPE_MANAGER_ID = ExtendedDrawers.id("compression_recipe_manager");

    public static void register() {
        var loader = DataResourceLoader.get();
        loader.registerReloadListener(COMPRESSION_OVERRIDES_ID, CompressionOverrideLoader::new);
        loader.registerReloadListener(COMPRESSION_RECIPE_MANAGER_ID, _ ->
                (state, _, preparationBarrier, reloadExecutor) -> preparationBarrier.wait(Unit.INSTANCE).thenRunAsync(() -> {
                    var recipeManager = state.get(DataResourceLoader.RECIPE_MANAGER_KEY);
                    var compressionLadders = state.get(CompressionOverrideLoader.STATE_KEY);

                    var compressionRecipeManager = new ServerCompressionRecipeManager(recipeManager, compressionLadders);

                    state.get(DataResourceLoader.DATA_RESOURCE_STORE_KEY).put(ServerCompressionRecipeManager.DATA_RESOURCE_STORE_KEY, compressionRecipeManager);
                }, reloadExecutor)
        );

        loader.addListenerOrdering(COMPRESSION_OVERRIDES_ID, COMPRESSION_RECIPE_MANAGER_ID);
        loader.addListenerOrdering(ResourceReloaderKeys.Server.RECIPES, COMPRESSION_RECIPE_MANAGER_ID);
    }
}
