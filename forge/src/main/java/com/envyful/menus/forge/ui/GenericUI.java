package com.envyful.menus.forge.ui;

import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.menus.forge.MenusForge;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.Map;

public class GenericUI {

    private EnvyPlayer<EntityPlayerMP> player;
    private Pane pane;

    public GenericUI(EnvyPlayer<EntityPlayerMP> player, String name, int height, Map<Pair<Integer, Integer>, Displayable> elements,
                     List<String> closeCommands) {
        this.player = player;
        this.pane = GuiFactory.paneBuilder().topLeftX(0).topLeftY(0).width(9).height(height).build();

        this.placeElements(elements);

        GuiFactory.guiBuilder()
                .title(name)
                .addPane(this.pane)
                .setPlayerManager(MenusForge.getInstance().getPlayerManager())
                .setCloseConsumer(envyPlayer -> this.handleClose(closeCommands))
                .build().open(player);
    }

    private void handleClose(List<String> commands) {
        UtilForgeConcurrency.runSync(() -> {
            for (String command : commands) {
                command = command.replace("%player%", this.player.getParent().getName());

                if (command.startsWith("console:")) {
                    command = command.split("console:")[1];

                    UtilForgeServer.executeCommand(command);
                } else {
                    command = command.split("player:")[1];

                    this.player.executeCommand(command);
                }
            }
        });
    }

    private void placeElements(Map<Pair<Integer, Integer>, Displayable> elements) {
        for (Map.Entry<Pair<Integer, Integer>, Displayable> integerElementEntry : elements.entrySet()) {
            this.pane.set(integerElementEntry.getKey().getX(), integerElementEntry.getKey().getY(), integerElementEntry.getValue());
        }
    }
}
