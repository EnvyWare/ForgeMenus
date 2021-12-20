package com.envyful.menus.forge.command;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.Menu;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class MenuAliasCommand extends CommandBase {

    private final Menu menu;

    public MenuAliasCommand(Menu menu) {
        this.menu = menu;
    }

    @Override
    public String getName() {
        return this.menu.getIdentifier();
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + this.menu.getIdentifier();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;

        if (!UtilPlayer.hasPermission(player, this.menu.getPermission())) {
            sender.sendMessage(new TextComponentString(UtilChatColour.translateColourCodes('&', MenusForge.getInstance().getLocale().getNoPermission())));
            return;
        }

        this.menu.open(MenusForge.getInstance().getPlayerManager().getPlayer(player));
    }
}
