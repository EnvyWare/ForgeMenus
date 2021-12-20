
package com.envyful.menus.forge.config;

import com.envyful.api.config.data.ConfigPath;
import com.envyful.api.config.yaml.AbstractYamlConfig;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
@ConfigPath("config/Menus/locale.yml")
public class MenusLocale extends AbstractYamlConfig {

    private String noPermission = "&c&l(!) &cYou cannot open that menu!";
    private String forceOpened = "&e&l(!) &eForced %target% to open %menu%";

    public MenusLocale() {
    }

    public String getNoPermission() {
        return this.noPermission;
    }

    public String getForceOpened() {
        return this.forceOpened;
    }
}
