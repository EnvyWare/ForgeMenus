package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.Menu;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "reload",
        description = "Reloads a menu - /menus reload <name>"
)
@Permissible("menus.command.reload")
@Child
public class ReloadCommand {

    @CommandProcessor
    protected void run(@Sender ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString("Reloads a menu - /menus reload <name>"));
            return;
        }

        Menu menu = MenusForge.getInstance().getMenu(args[0]);

        if(menu == null) {
            sender.sendMessage(new TextComponentString("Menu doesn't exist!"));
            return;
        }

        menu.reloadConfig();
        menu.loadItems();
        sender.sendMessage(new TextComponentString("Menu reloaded."));
    }
}
