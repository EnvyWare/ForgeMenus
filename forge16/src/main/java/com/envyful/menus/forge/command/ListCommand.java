package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.menus.forge.MenusForge;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

@Command(
        value = "list",
        description = "Lists all menus"
)
@Permissible("menus.command.list")
@Child
public class ListCommand {

    @CommandProcessor
    public void run(@Sender ICommandSource sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage(new StringTextComponent("/menus list - Lists all menus"), Util.DUMMY_UUID);
            return;
        }

        sender.sendMessage(new StringTextComponent("Menus:"), Util.DUMMY_UUID);

        for (String menuName : MenusForge.getInstance().getLoadedNames()) {
            sender.sendMessage(new StringTextComponent(" - " + menuName), Util.DUMMY_UUID);
        }
    }
}
