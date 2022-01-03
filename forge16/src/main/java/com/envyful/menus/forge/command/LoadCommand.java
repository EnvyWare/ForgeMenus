package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.config.MenuConfig;
import com.envyful.menus.forge.data.Menu;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

import java.io.File;

@Command(
        value = "load",
        description = "Loads menus from file(s) - /menus load <file>"
)
@Permissible("menus.command.load")
@Child
public class LoadCommand {

    @CommandProcessor
    public void run(@Sender ICommandSource sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new StringTextComponent("Loads menus from file(s) - /menus load <file>"), Util.DUMMY_UUID);
            return;
        }

        File file = new File(MenuConfig.PATH + File.separator + args[0]);

        if(!file.exists()) {
            sender.sendMessage(
                    new StringTextComponent("File doesn't exist! example file: 'example.yml' " +
                            "(config/Menus/menus/example.yml) example directory: 'idiot/' (config/Menus/menus/idiot/)"), Util.DUMMY_UUID);
            return;
        }

        if(file.isDirectory()) {
            MenusForge.getInstance().handleDirectory(file);
        }else {
            String name = file.getPath().replace((MenuConfig.PATH + File.separator), "").replace(".yml", "");
            Menu menu = new Menu(name);

            MenusForge.getInstance().addMenu(menu);
        }

        sender.sendMessage(new StringTextComponent("Menu loaded"), Util.DUMMY_UUID);
    }
}
