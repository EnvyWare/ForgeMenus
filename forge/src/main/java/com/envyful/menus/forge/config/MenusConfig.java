package com.envyful.menus.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@ConfigPath("config/Menus/config.yml")
public class MenusConfig extends AbstractYamlConfig {

    private String scriptingEngine = "groovy";

    public MenusConfig() {
    }

    public String getScriptingEngine() {
        return this.scriptingEngine;
    }
}
