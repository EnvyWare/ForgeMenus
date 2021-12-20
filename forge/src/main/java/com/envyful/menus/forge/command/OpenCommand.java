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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "open",
        description = "Opens the GUI - /menus open <name>"
)
@Permissible("menus.command.open")
@Child
public class OpenCommand {

    @CommandProcessor
    public void run(@Sender EntityPlayerMP sender, @Completable(MenuTabCompleter.class) @Argument Menu menu) {
        if (!UtilPlayer.hasPermission(sender, menu.getPermission())) {
            sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    "&c&l(!) &cYou don't have permission for this menu")));
            return;
        }

        menu.open(MenusForge.getInstance().getPlayerManager().getPlayer(sender));
    }
}
