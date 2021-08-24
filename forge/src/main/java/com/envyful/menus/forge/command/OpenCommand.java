package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.Menu;
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
    protected void run(@Sender EntityPlayerMP sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString("Opens the gui - /menus open <name>"));
            return;
        }

        Menu menu = MenusForge.getInstance().getMenu(args[0]);

        if(menu == null) {
            sender.sendMessage(new TextComponentString("Menu doesn't exist!"));
            return;
        }

        menu.open(MenusForge.getInstance().getPlayerManager().getPlayer(sender));
    }
}
