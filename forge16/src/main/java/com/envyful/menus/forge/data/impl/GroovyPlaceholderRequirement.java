package com.envyful.menus.forge.data.impl;

import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.ItemRequirement;
import com.envyful.menus.forge.data.data.Requirement;
import com.envyful.papi.api.util.UtilPlaceholder;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.configurate.ConfigurationNode;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

@Requirement("code")
public class GroovyPlaceholderRequirement implements ItemRequirement {

    private final String code;

    public GroovyPlaceholderRequirement(ConfigurationNode node) {
        this.code = node.node("code").getString();
    }

    @Override
    public boolean fits(ServerPlayerEntity player) {
        if (this.code == null || this.code.isEmpty()) {
            return true;
        }

        ScriptEngine engine = MenusForge.getInstance().getConfig().getEngine();

        if (engine == null) {
            return false;
        }

        try {
            return (boolean) engine.eval(UtilPlaceholder.replaceIdentifiers(player, this.code.replace("tick",
                    ServerLifecycleHooks.getCurrentServer().getTickCounter() + "")));
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return false;
    }
}
