package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.Menu;
import com.envyful.menus.forge.data.MenuTabCompleter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;

@Command(
        value = "open",
        description = "Opens the GUI - /menus open <name>"
)
@Permissible("menus.command.open")
@Child
public class OpenCommand {

    @CommandProcessor
    public void run(@Sender ServerPlayerEntity sender, @Completable(MenuTabCompleter.class) @Argument Menu menu, String[] args) {
        if (!UtilPlayer.hasPermission(sender, menu.getPermission())) {
            sender.sendMessage(UtilChatColour.colour(MenusForge.getInstance().getLocale().getNoPermission()), Util.DUMMY_UUID);
            return;
        }

        menu.open(MenusForge.getInstance().getPlayerManager().getPlayer(sender));
    }
}
