package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.Menu;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

@Command(
        value = "open",
        description = "Opens the GUI - /menus open <name>"
)
@Permissible("menus.command.open")
@Child
public class OpenCommand {

    @CommandProcessor
    public void run(@Sender EntityPlayerMP sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString("Opens the gui - /menus open <name>"));
            return;
        }

        Menu menu = MenusForge.getInstance().getMenu(args[0]);

        if(menu == null) {
            sender.sendMessage(new TextComponentString("Menu doesn't exist!"));
            return;
        }

        if (!PermissionAPI.hasPermission(sender, menu.getPermission())
                && !sender.canUseCommand(4, menu.getPermission())) {
            sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&',
                    "&c&l(!) &cYou don't have permission for this menu")));
            return;
        }

        menu.open(MenusForge.getInstance().getPlayerManager().getPlayer(sender));
    }
}
