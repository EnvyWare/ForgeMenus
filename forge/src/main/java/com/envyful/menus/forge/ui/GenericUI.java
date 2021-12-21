package com.envyful.menus.forge.ui;

import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.gui.ForgeGuiTracker;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.pane.Pane;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.ConfigItem;
import com.envyful.menus.forge.data.Menu;
import com.envyful.menus.forge.data.task.MenuUpdateTask;
import com.envyful.papi.api.util.UtilPlaceholder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.Map;

public class GenericUI {

    private Menu menu;
    private EnvyPlayer<EntityPlayerMP> player;
    private Pane pane;
    private String name;
    private int height;
    private boolean allowClose;
    private boolean clicked = false;
    private int updateTicks;
    private long lastUpdate = FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();

    public GenericUI(Menu menu, EnvyPlayer<EntityPlayerMP> player, String name, int height, int updateTicks,
                     boolean allowClose, Map<Pair<Integer, Integer>, ConfigItem> elements,
                     List<String> closeCommands) {
        this.menu = menu;
        this.player = player;
        this.allowClose = allowClose;
        this.name = name;
        this.height = height;
        this.updateTicks = updateTicks;
        this.pane = GuiFactory.paneBuilder().topLeftX(0).topLeftY(0).width(9).height(height).build();

        this.placeElements(elements);

        GuiFactory.guiBuilder()
                .title(UtilChatColour.translateColourCodes('&',
                        UtilPlaceholder.replaceIdentifiers(this.player.getParent(), name)))
                .addPane(this.pane)
                .height(height)
                .setPlayerManager(MenusForge.getInstance().getPlayerManager())
                .setCloseConsumer(envyPlayer -> this.handleClose(closeCommands))
                .build().open(player);
    }

    public void replaceItems() {
        if (!this.canReplace()) {
            return;
        }

        this.placeElements(this.menu.getItems());
    }

    public boolean canReplace() {
        if (this.updateTicks == -1) {
            return false;
        }

        return (FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter() - this.lastUpdate) >= this.updateTicks;
    }

    private void handleClose(List<String> commands) {
        if (!this.allowClose) {
            UtilForgeConcurrency.runSync(() -> {
                GuiFactory.guiBuilder()
                        .title(UtilChatColour.translateColourCodes('&',
                                UtilPlaceholder.replaceIdentifiers(this.player.getParent(), name)))
                        .addPane(this.pane)
                        .height(height)
                        .setPlayerManager(MenusForge.getInstance().getPlayerManager())
                        .setCloseConsumer(envyPlayer -> this.handleClose(commands))
                        .build().open(player);
            });
            return;
        }

        MenuUpdateTask.removeOpenUI(player.getParent());

        UtilForgeConcurrency.runSync(() -> {
            for (String command : commands) {
                command = UtilPlaceholder.replaceIdentifiers(this.player.getParent(), command);

                if (command.startsWith("console:")) {
                    String[] split = command.split("console:");

                    if (split.length < 2) {
                        continue;
                    }

                    command = split[1];

                    UtilForgeServer.executeCommand(command);
                } else {
                    String[] split = command.split("player:");

                    if (split.length < 2) {
                        continue;
                    }
                    command = split[1];

                    this.player.executeCommand(command);
                }
            }
        });
    }

    private void placeElements(Map<Pair<Integer, Integer>, ConfigItem> elements) {
        for (Map.Entry<Pair<Integer, Integer>, ConfigItem> integerElementEntry : elements.entrySet()) {
            this.pane.set(integerElementEntry.getKey().getX(), integerElementEntry.getKey().getY(),
                    integerElementEntry.getValue().build(this.player.getParent(), this));
        }

        ForgeGuiTracker.enqueueUpdate(this.player);
    }

    public void setAllowClose(boolean allowClose) {
        this.allowClose = allowClose;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public boolean isClicked() {
        return this.clicked;
    }
}
