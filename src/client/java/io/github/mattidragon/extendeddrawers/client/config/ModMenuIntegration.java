package io.github.mattidragon.extendeddrawers.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.mattidragon.extendeddrawers.config.ExtendedDrawersConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ConfigClient.createScreen(parent, ExtendedDrawersConfig.get(), ExtendedDrawersConfig::set);
    }
}
