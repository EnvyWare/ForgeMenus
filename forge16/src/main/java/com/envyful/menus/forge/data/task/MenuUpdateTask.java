package com.envyful.menus.forge.data.task;

import com.envyful.menus.forge.ui.GenericUI;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Map;
import java.util.UUID;

public class MenuUpdateTask implements Runnable {

    private static final Map<UUID, GenericUI> OPEN_UIS = Maps.newConcurrentMap();

    public static void addOpenUI(ServerPlayerEntity player, GenericUI ui) {
        OPEN_UIS.put(player.getUniqueID(), ui);
    }

    public static void removeOpenUI(ServerPlayerEntity player) {
        OPEN_UIS.remove(player.getUniqueID());
    }

    @Override
    public void run() {
        for (GenericUI value : OPEN_UIS.values()) {
            value.replaceItems();
        }
    }
}
