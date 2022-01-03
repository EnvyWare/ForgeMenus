package com.envyful.menus.forge.command;

import com.envyful.api.command.annotate.Child;
import com.envyful.api.command.annotate.Command;
import com.envyful.api.command.annotate.Permissible;
import com.envyful.api.command.annotate.executor.CommandProcessor;
import com.envyful.api.command.annotate.executor.Sender;
import com.envyful.api.config.util.UtilConfig;
import com.envyful.api.forge.items.ItemFlag;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.type.UtilParse;
import com.envyful.menus.forge.config.MenuConfig;
import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ParsingException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(
        value = "convert",
        description = "Converts a menu from VirtualChest - /menus convert <file path> <new file path>"
)
@Permissible("menus.command.convert")
@Child
public class ConvertCommand {

    private static final Pattern SLOT_PATTERN = Pattern.compile("(Slot)([0-9]+)");

    @CommandProcessor
    public void run(@Sender ICommandSource sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(new StringTextComponent("Loads menus from file(s) - /menus load <file>"), Util.DUMMY_UUID);
            return;
        }

        File file = new File(args[0]);

        if(!file.exists()) {
            sender.sendMessage(
                    new StringTextComponent("File doesn't exist! example file: 'config/VirtualChest/example.conf'"), Util.DUMMY_UUID);
            return;
        }

        File target = new File(args[1]);

        if (target.exists()) {
            sender.sendMessage(new StringTextComponent("That target file already exists!"), Util.DUMMY_UUID);
            return;
        }

        CommentedConfigurationNode loaded = this.loadNode(file);

        if (loaded == null) {
            sender.sendMessage(new StringTextComponent("Error loading the file.... Contact developer"), Util.DUMMY_UUID);
            return;
        }

        CommentedConfigurationNode menuData = loaded.node("virtualchest");
        MenuConfig menuConfig = new MenuConfig(args[1]);
        CommentedConfigurationNode inventory = menuConfig.getNode().node("inventory");

        try {
            inventory.parent().node("_comment").set("AUTO GENERATED MENU CONFIG FROM VIRTUAL CHEST FILE: " + args[0] + "\n" +
                    "\n" +
                    "If there is an issue with the generation of this file please seek assistance in my Discord: \n" +
                    "https://discord.gg/7vqgtrjDGw\n");
            inventory.node("identifier").set(args[0]);
            inventory.node("close-commands").set(Lists.newArrayList());
            inventory.node("name").set(menuData.node("TextTitle").getString("No Title Given"));
            inventory.node("height").set(menuData.node("Rows").getInt(3));
            inventory.node("update-ticks").set(menuData.node("UpdateIntervalTick").getInt(-1));
            inventory.removeChild("items");

            for (Map.Entry<Object, CommentedConfigurationNode> entries : menuData.childrenMap().entrySet()) {
                String key = (String) entries.getKey();
                Matcher matcher = SLOT_PATTERN.matcher(key);

                if (!matcher.matches()) {
                    continue;
                }

                int slot = Integer.parseInt(matcher.group(2));
                ConfigurationNode node = inventory.node("items").node("slot-" + slot);

                if (!entries.getValue().childrenList().isEmpty()) {
                    for (CommentedConfigurationNode commentedConfigurationNode : entries.getValue().childrenList()) {
                        this.handleChildrenMap(slot, node, commentedConfigurationNode);
                    }
                }

                if (!entries.getValue().childrenMap().isEmpty()) {
                    this.handleChildrenMap(slot, node, entries.getValue());
                }
            }

            menuConfig.save();
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    private CommentedConfigurationNode loadNode(File file) {
        try {
            return HoconConfigurationLoader.builder().file(file).build().load(ConfigurationOptions.defaults());
        } catch (ParsingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void handleChildrenMap(int slot, ConfigurationNode newNode, CommentedConfigurationNode node) throws SerializationException {
        for (Map.Entry<Object, CommentedConfigurationNode> entry : node.childrenMap().entrySet()) {
            switch (((String) entry.getKey()).toLowerCase()) {
                case "item" : this.handleItem(slot, newNode, entry.getValue()); break;
                case "action" : case "primaryaction" : this.handleAction(newNode, Displayable.ClickType.RIGHT, entry.getValue()); break;
                case "secondaryaction" : this.handleAction(newNode, Displayable.ClickType.LEFT, entry.getValue()); break;
                case "primaryshiftaction" : this.handleAction(newNode, Displayable.ClickType.SHIFT_RIGHT, entry.getValue()); break;
                case "secondaryshiftaction" : this.handleAction(newNode, Displayable.ClickType.SHIFT_LEFT, entry.getValue()); break;
                default : break;
            }
        }
    }

    private void handleItem(int slot, ConfigurationNode node, CommentedConfigurationNode oldData) throws SerializationException {
        if (node.hasChild("positionX")) {
            node = node.node("else");
        }

        node.node("positionX").set(slot % 9);
        node.node("positionY").set(slot / 9);
        node.node("type").set(oldData.node("ItemType").getString("minecraft:dirt"));
        node.node("name").set(oldData.node("DisplayName").getString(" "));
        node.node("amount").set(oldData.node("Count").getInt(1));
        node.node("damage").set(oldData.node("UnsafeDamage").getInt(0));
        node.node("tooltip").set("");
        node.node("lore").set(UtilConfig.getList(oldData, String.class, "ItemLore"));
        int counter = 0;

        for (String itemEnchantments : UtilConfig.getList(oldData, String.class, "ItemEnchantments")) {
            String[] data = itemEnchantments.split(":");
            StringBuilder enchantType;
            int level = 1;

            if (data.length == 2) {
                enchantType = new StringBuilder(data[0]);
                int value = UtilParse.parseInteger(data[1]).orElse(-1);

                if (value == -1) {
                    enchantType.append(data[1]);
                } else {
                    level = value;
                }
            } else if (data.length == 3) {
                enchantType = new StringBuilder(data[0]).append(data[1]);
                level = UtilParse.parseInteger(data[2]).orElse(0);
            } else {
                enchantType = new StringBuilder(data[0]);
            }

            node.node("enchants", "enchant-" + counter, "id").set(enchantType);
            node.node("enchants", "enchant-" + counter, "level").set(level);
        }

        List<String> flags = Lists.newArrayList();

        if (oldData.node("HideEnchantments").getBoolean(false)) {
            flags.add(ItemFlag.HIDE_ENCHANTS.name());
        }

        if (oldData.node("HideAttributes").getBoolean(false)) {
            flags.add(ItemFlag.HIDE_MODIFIERS.name());
        }

        node.node("flags").set(flags);
        node.node("requirements", "one", "type").set("code");
        node.node("requirements", "one", "code").set(oldData.parent().node("Requirements").getString(""));
    }

    private void handleAction(ConfigurationNode node, Displayable.ClickType clickType, CommentedConfigurationNode oldData) throws SerializationException {
        int id = node.node("click-commands").childrenMap().size();

        node.node("click-commands", "command-" + id, "types").set(Lists.newArrayList(clickType.name()));
        List<String> commands = Lists.newArrayList();
        for (String command : oldData.node("Command").getString("").split(";")) {
            commands.add(command.trim());
        }

        node.node("click-commands", "command-" + id, "commands").set(commands);
    }
}
