
package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.Argument;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Completable;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.command.completion.player.ExcludeSelfCompletion;
import com.envyful.api.forge.command.completion.player.PlayerTabCompleter;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.Menu;
import com.envyful.menus.forge.data.MenuTabCompleter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

@Command(
        value = "forceopen",
        description = "Opens the GUI for target player - /menus open <name> <player>"
)
@Permissible("menus.command.open.force")
@Child
public class ForceOpenCommand {

    @CommandProcessor
    public void run(@Sender ServerPlayerEntity sender, @Completable(MenuTabCompleter.class) @Argument Menu menu,
                    @Completable(PlayerTabCompleter.class) @ExcludeSelfCompletion @Argument ServerPlayerEntity target) {
        menu.open(MenusForge.getInstance().getPlayerManager().getPlayer(target));
        sender.sendMessage(new StringTextComponent(UtilChatColour.translateColourCodes('&',
                MenusForge.getInstance().getLocale().getForceOpened()
                        .replace("%target%", target.getName().getString())
                        .replace("%menu%", menu.getIdentifier()))), Util.DUMMY_UUID);
    }
}
