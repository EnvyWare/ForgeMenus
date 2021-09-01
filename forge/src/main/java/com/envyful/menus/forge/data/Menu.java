package com.envyful.menus.forge.data;

import com.envyful.api.config.util.UtilConfig;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.api.type.Pair;
import com.envyful.menus.forge.config.MenuConfig;
import com.envyful.menus.forge.ui.GenericUI;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Map;

public class Menu {

    private final String fileIdentifier;

    private MenuConfig config;
    private String identifier;
    private String name;
    private int height;
    private String permission;
    private List<String> closeCommands;
    private List<String> openCommands;
    private Map<Pair<Integer, Integer>, Displayable> items;

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
        this.permission = this.config.getNode().node("inventory", "permission").getString("menu." + this.identifier);
        this.closeCommands = UtilConfig.getList(this.config.getNode(), String.class, "inventory", "close-commands");
        this.openCommands = UtilConfig.getList(this.config.getNode(), String.class, "inventory", "open-commands");
        this.items = Maps.newHashMap();

        for (ConfigurationNode value : this.config.getNode().node("inventory", "items").childrenMap().values()) {
            int positionX = value.node("positionX").getInt(1);
            int positionY = value.node("positionY").getInt(1);
            Pair<Integer, Integer> position = Pair.of(positionX, positionY);
            Item itemType = Item.getByNameOrId(value.node("type").getString("minecraft:dirt"));
            int amount = value.node("amount").getInt(1);
            String name = value.node("name").getString();
            int damage = value.node("damage").getInt(0);
            String tooltip = value.node("tooltip").getString("");
            List<String> lore = UtilConfig.getList(value, String.class, "lore");
            List<String> commands = UtilConfig.getList(value, String.class, "commands");

            ItemStack itemStack = new ItemBuilder()
                    .type(itemType == null ? Items.SKULL : itemType)
                    .amount(amount)
                    .name(name)
                    .lore(lore)
                    .damage(damage)
                    .build();

            if (itemType == null) {
                if (value.node("type").getString().contains("basehead")) {
                    String base64 = value.node("type").getString().split("-")[1];
                    itemStack.getOrCreateSubCompound("SkullOwner").setString("textures", base64);
                }
            }

            itemStack.getOrCreateSubCompound("UnsafeData").setString("tooltip", tooltip);

            for (ConfigurationNode nbtNode : value.node("nbt").childrenMap().values()) {
                switch (nbtNode.node("type").getString("String").toLowerCase()) {
                    case "int":
                        itemStack.getTagCompound().setInteger(nbtNode.node("key").getString(""), nbtNode.node("value").getInt(0));
                        break;
                    case "short":
                        itemStack.getTagCompound().setShort(nbtNode.node("key").getString(""), (short) nbtNode.node("value").getInt(0));
                        break;
                    case "byte":
                        itemStack.getTagCompound().setByte(nbtNode.node("key").getString(""), (byte) nbtNode.node("value").getInt(0));
                        break;
                    case "long":
                        itemStack.getTagCompound().setLong(nbtNode.node("key").getString(""), nbtNode.node("value").getLong(0));
                        break;
                    default: case "string":
                        itemStack.getTagCompound().setString(nbtNode.node("key").getString(""), nbtNode.node("value").getString(""));
                        break;
                }
            }

            List<Pair<Integer, Integer>> positions = Lists.newArrayList();

            for (int slots : UtilConfig.getList(value, Integer.class, "slots")) {
                positions.add(Pair.of(slots % 9, slots / 9));
            }

            Displayable button = GuiFactory.displayableBuilder(ItemStack.class).itemStack(itemStack)
                    .clickHandler((envyPlayer, clickType) ->
                            this.handleClick((EnvyPlayer<EntityPlayerMP>) envyPlayer, clickType, commands)).build();

            for (Pair<Integer, Integer> pos : positions) {
                this.items.put(pos, button);
            }
        }
    }

    private void handleClick(EnvyPlayer<EntityPlayerMP> player, Displayable.ClickType clickType, List<String> commands) {
        for (String command : commands) {
            if (command.equalsIgnoreCase("%close%")) {
                player.getParent().closeScreen();
                continue;
            }

            if (command.isEmpty() || !(command.contains("player:") || command.contains("console:"))) {
                continue;
            }

            command = UtilPlaceholder.replaceIdentifiers(player.getParent(), command);

            if (command.startsWith("console:")) {
                command = command.split("console:")[1];

                UtilForgeServer.executeCommand(command);
            } else {
                command = command.split("player:")[1];

                player.executeCommand(command);
            }
        }
    }

    public void open(EnvyPlayer<EntityPlayerMP> player) {
        new GenericUI(player, this.name, this.height, this.items, this.closeCommands);

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
