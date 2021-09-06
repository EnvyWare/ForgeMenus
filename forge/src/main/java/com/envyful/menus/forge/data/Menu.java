package com.envyful.menus.forge.data;

import com.envyful.api.config.util.UtilConfig;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.menus.forge.config.MenuConfig;
import com.envyful.menus.forge.ui.GenericUI;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Map;

public class Menu {

    private final String fileIdentifier;

    private MenuConfig config;
    private String identifier;
    private String name;
    private int height;
    private boolean allowNaturalClose;
    private String permission;
    private List<String> closeCommands;
    private List<String> openCommands;
    private Map<Pair<Integer, Integer>, ConfigItem> items;

    public Menu(String fileIdentifier) {
        this.fileIdentifier = fileIdentifier;
        this.reloadConfig();
        this.loadItems();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getPermission() {
        return this.permission;
    }

    public void reloadConfig() {
        this.config = new MenuConfig(fileIdentifier + ".yml");
    }

    public void loadItems() {
        this.identifier = this.config.getNode().node("inventory", "identifier").getString();
        this.name = this.config.getNode().node("inventory", "name").getString();
        this.height = this.config.getNode().node("inventory", "height").getInt();
        this.allowNaturalClose = this.config.getNode().node("inventory", "allow-natural-close").getBoolean(true);
        this.permission = this.config.getNode().node("inventory", "permission").getString("menu." + this.identifier);
        this.closeCommands = UtilConfig.getList(this.config.getNode(), String.class, "inventory", "close-commands");
        this.openCommands = UtilConfig.getList(this.config.getNode(), String.class, "inventory", "open-commands");
        this.items = Maps.newHashMap();

        for (ConfigurationNode value : this.config.getNode().node("inventory", "items").childrenMap().values()) {
            int positionX = value.node("positionX").getInt(1);
            int positionY = value.node("positionY").getInt(1);
            Pair<Integer, Integer> position = Pair.of(positionX, positionY);

            List<Integer> slots1 = UtilConfig.getList(value, Integer.class, "slots");

            if (slots1.isEmpty()) {
                this.items.put(position, new ConfigItem(value));
            } else {
                List<Pair<Integer, Integer>> positions = Lists.newArrayList();

                for (int slots : slots1) {
                    positions.add(Pair.of(slots % 9, slots / 9));
                }

                for (Pair<Integer, Integer> pos : positions) {
                    this.items.put(pos, new ConfigItem(value));
                }
            }
        }
    }

    public void open(EnvyPlayer<EntityPlayerMP> player) {
        new GenericUI(player, this.name, this.height, this.allowNaturalClose, this.items, this.closeCommands);

        UtilForgeConcurrency.runSync(() -> {
            for (String command : this.openCommands) {
                command = UtilPlaceholder.replaceIdentifiers(player.getParent(), command);

                if (command.startsWith("console:")) {
                    command = command.split("console:")[1];

                    UtilForgeServer.executeCommand(command);
                } else {
                    command = command.split("player:")[1];

                    player.executeCommand(command);
                }
            }
        });
    }
}
