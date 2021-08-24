package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.TextComponentString;

@Command(
        value = "menus",
        description = "/menus reload <name> | /menu load <path> | /menu list | /menu open <name>"
)
@Permissible("menus.command.menu")
@SubCommands({
        ListCommand.class,
        LoadCommand.class,
        OpenCommand.class,
        ReloadCommand.class
})
public class MenuCommand {

    @CommandProcessor
    protected void run(@Sender ICommandSender sender, String[] args) {
        sender.sendMessage(new TextComponentString("/menus reload <name> | /menu load <path> | /menu list | /menu open <name>"));
    }
}
