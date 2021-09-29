package com.envyful.menus.forge.data;

import com.envyful.api.command.injector.TabCompleter;
import com.envyful.menus.forge.MenusForge;
import net.minecraft.entity.player.EntityPlayerMP;

import java.lang.annotation.Annotation;
import java.util.List;

public class MenuTabCompleter implements TabCompleter<Menu, EntityPlayerMP> {
    @Override
    public Class<EntityPlayerMP> getSenderClass() {
        return EntityPlayerMP.class;
    }

    @Override
    public Class<Menu> getCompletedClass() {
        return Menu.class;
    }

    @Override
    public List<String> getCompletions(EntityPlayerMP sender, String[] currentData, Annotation... completionData) {
        return MenusForge.getInstance().getLoadedNames();
    }
}
