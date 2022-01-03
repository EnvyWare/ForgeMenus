package com.envyful.menus.forge.command;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.Menu;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class MenuAliasCommand { //TODO:

    private final Menu menu;

    public MenuAliasCommand(Menu menu) {
        this.menu = menu;
    }

    public String getName() {
        return this.menu.getIdentifier();
    }

    public List<String> getAliases() {
        return this.menu.getCommandAliases();
    }

    public String getUsage(ICommandSource sender) {
        return "/" + this.menu.getIdentifier();
    }

    public void execute(MinecraftServer server, ICommandSource sender, String[] args) throws CommandException {
        if (!(sender instanceof ServerPlayerEntity)) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) sender;

        if (!UtilPlayer.hasPermission(player, this.menu.getPermission())) {
            sender.sendMessage(new StringTextComponent(UtilChatColour.translateColourCodes('&', MenusForge.getInstance().getLocale().getNoPermission())), Util.DUMMY_UUID);
            return;
        }

        this.menu.open(MenusForge.getInstance().getPlayerManager().getPlayer(player));
    }
}
