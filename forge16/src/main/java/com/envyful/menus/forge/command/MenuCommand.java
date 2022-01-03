package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.SubCommands;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

@Command(
        value = "menus",
        description = "/menus reload <name> | /menu load <path> | /menu list | /menu open <name>",
        aliases = {
                "menu"
        }
)
@Permissible("menus.command.menu")
@SubCommands({
        ListCommand.class,
        LoadCommand.class,
        OpenCommand.class,
        ReloadCommand.class,
        ForceOpenCommand.class,
        ConvertCommand.class
})
public class MenuCommand {

    @CommandProcessor
    public void run(@Sender ICommandSource sender, String[] args) {
        sender.sendMessage(new StringTextComponent("/menus reload <name> | /menu load <path> | /menu list | /menu open <name>"), Util.DUMMY_UUID);
    }
}
