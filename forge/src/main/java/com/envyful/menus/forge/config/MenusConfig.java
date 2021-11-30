package com.envyful.menus.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@ConfigSerializable
@ConfigPath("config/Menus/config.yml")
public class MenusConfig extends AbstractYamlConfig {

    private String scriptingEngine = "groovy";
    private ScriptEngine engine = null;

    public MenusConfig() {
    }

    public ScriptEngine getEngine() {
        if (this.engine == null) {
            ScriptEngineManager manager = new ScriptEngineManager();
            this.engine = manager.getEngineByName("groovy");
        }

        return this.engine;
    }
}
