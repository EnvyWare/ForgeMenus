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
        value = "reload",
        description = "Reloads a menu - /menus reload <name>"
)
@Permissible("menus.command.reload")
@Child
public class ReloadCommand {

    @CommandProcessor
    public void run(@Sender ICommandSource sender, String[] args) {
        if (args.length != 1 || args[0].equalsIgnoreCase("all")) {
            sender.sendMessage(new StringTextComponent("Reloading all menus...."), Util.DUMMY_UUID);
            MenusForge.getInstance().unloadAll();
            MenusForge.getInstance().handleDirectory(new File(MenuConfig.PATH));
            sender.sendMessage(new StringTextComponent("Reloaded (and loaded) all menus...."), Util.DUMMY_UUID);
            return;
        }

        Menu menu = MenusForge.getInstance().getMenu(args[0]);

        if(menu == null) {
            sender.sendMessage(new StringTextComponent("Menu doesn't exist!"), Util.DUMMY_UUID);
            return;
        }

        menu.reloadConfig();
        menu.loadItems();
        sender.sendMessage(new StringTextComponent("Menu reloaded."), Util.DUMMY_UUID);
    }
}
