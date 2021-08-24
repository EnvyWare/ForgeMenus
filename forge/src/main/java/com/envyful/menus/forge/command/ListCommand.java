package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.menus.forge.MenusForge;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "list",
        description = "Lists all menus"
)
@Permissible("menus.command.list")
@Child
public class ListCommand {

    @CommandProcessor
    protected void run(@Sender ICommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(new TextComponentString("/menus list - Lists all menus"));
            return;
        }

        sender.sendMessage(new TextComponentString("Menus:"));

        for (String menuName : MenusForge.getInstance().getLoadedNames()) {
            sender.sendMessage(new TextComponentString(" - " + menuName));
        }
    }
}
