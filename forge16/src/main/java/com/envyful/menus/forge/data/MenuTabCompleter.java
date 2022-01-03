package com.envyful.menus.forge.data;

import com.envyful.api.command.injector.TabCompleter;
import com.envyful.menus.forge.MenusForge;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.lang.annotation.Annotation;
import java.util.List;

public class MenuTabCompleter implements TabCompleter<Menu, ServerPlayerEntity> {
    @Override
    public Class<ServerPlayerEntity> getSenderClass() {
        return ServerPlayerEntity.class;
    }

    @Override
    public Class<Menu> getCompletedClass() {
        return Menu.class;
    }

    @Override
    public List<String> getCompletions(ServerPlayerEntity sender, String[] currentData, Annotation... completionData) {
        return MenusForge.getInstance().getLoadedNames();
    }
}
