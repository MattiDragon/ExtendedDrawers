package io.github.mattidragon.extendeddrawers.client.config;

import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigScreenFactory.createScreen(parent, ExtendedDrawers.CONFIG.get(), ExtendedDrawers.CONFIG::set);
    }
}
