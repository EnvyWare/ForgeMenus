package com.envyful.menus.forge.data;

import com.envyful.api.config.util.UtilConfig;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.api.forge.items.ItemFlag;
import com.envyful.api.forge.server.UtilForgeServer;
import com.envyful.api.gui.factory.GuiFactory;
import com.envyful.api.gui.item.Displayable;
import com.envyful.api.player.EnvyPlayer;
import com.envyful.menus.forge.MenusForge;
import com.envyful.menus.forge.data.impl.PermissionRequirement;
import com.envyful.menus.forge.ui.GenericUI;
import com.envyful.papi.api.util.UtilPlaceholder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConfigItem {

    private final Item itemType;
    private final int amount;
    private final String name;
    private final int damage;
    private final String tooltip;
    private final List<String> lore;
    private final Map<Displayable.ClickType, List<String>> commands;
    private final List<ItemRequirement> requirements;
    private final ConfigItem elseItem;
    private final ConfigurationNode node;

    public ConfigItem(ConfigurationNode value) {
        this.itemType = UtilConfigItem.fromNameOrId(value.node("type").getString("minecraft:dirt"));
        this.amount = value.node("amount").getInt(1);
        this.name = value.node("name").getString(" ");
        this.damage = value.node("damage").getInt(0);
        this.tooltip = value.node("tooltip").getString("");
        this.lore = UtilConfig.getList(value, String.class, "lore");
        this.requirements = Lists.newArrayList();
        this.commands = Maps.newHashMap();

        if (value.hasChild("commands")) {
            List<String> commandList = UtilConfig.getList(value, String.class, "commands");
            this.commands.put(Displayable.ClickType.RIGHT, commandList);
            this.commands.put(Displayable.ClickType.SHIFT_RIGHT, commandList);
            this.commands.put(Displayable.ClickType.LEFT, commandList);
            this.commands.put(Displayable.ClickType.SHIFT_LEFT, commandList);
            this.commands.put(Displayable.ClickType.MIDDLE, commandList);
        }

        if (value.hasChild("click-commands")) {
            for (ConfigurationNode commandNode : value.node("click-commands").childrenMap().values()) {
                List<String> commands = UtilConfig.getList(commandNode, String.class, "commands");

                for (String type : UtilConfig.getList(commandNode, String.class, "types")) {
                    Displayable.ClickType clickType = Displayable.ClickType.valueOf(type);
                    if (clickType != null) {
                        this.commands.computeIfAbsent(clickType, ___ -> Lists.newArrayList()).addAll(commands);
                    }
                }
            }
        }

        if (value.hasChild("requirement")) {
            this.requirements.add(new PermissionRequirement(value.node("requirement").getString()));
        }

        if (value.hasChild("requirements")) {
            for (ConfigurationNode configurationNode : value.node("requirements").childrenMap().values()) {
                ItemRequirement from = RequirementFactory.from(configurationNode);

                if (from != null) {
                    this.requirements.add(from);
                }
            }
        }

        if (!this.requirements.isEmpty()) {
            this.elseItem = new ConfigItem(value.node("else"));
        } else {
            this.elseItem = null;
        }

        this.node = value;
    }

    public Displayable build(ServerPlayerEntity player, GenericUI ui) {
        if (!this.requirements.isEmpty() && !this.canSee(player)) {
            return this.elseItem.build(player, ui);
        }

        ItemBuilder builder = new ItemBuilder()
                .type(itemType == null ? Items.PLAYER_HEAD : itemType)
                .amount(amount)
                .name(UtilChatColour.colour(UtilPlaceholder.replaceIdentifiers(player, this.name)))
                .lore(this.getLore(player));

        for (ConfigurationNode enchants : this.node.node("enchants").childrenMap().values()) {
            builder.enchant(Enchantment.getEnchantmentByID(enchants.node("id").getInt()),
                    enchants.node("level").getInt());
        }

        for (String flags : UtilConfig.getList(node, String.class, "flags")) {
            builder.itemFlag(ItemFlag.valueOf(flags));
        }

        ItemStack itemStack = builder.build();

        if (itemType == null) {
            if (this.node.node("type").getString().contains("basehead")) {
                String base64 = this.node.node("type").getString().split("-")[1];
                itemStack.getOrCreateChildTag("SkullOwner").putString("textures", base64);
            } else if (this.node.node("type").getString().equals("playerhead")) {
                itemStack.getTag().putString("SkullOwner", player.getName().getString());
            }
        }

        itemStack.getOrCreateChildTag("UnsafeData").putString("tooltip", tooltip);

        for (ConfigurationNode nbtNode : this.node.node("nbt").childrenMap().values()) {
            switch (nbtNode.node("type").getString("String").toLowerCase()) {
                case "int":
                    itemStack.getTag().putInt(nbtNode.node("key").getString(""), nbtNode.node("value").getInt(0));
                    break;
                case "short":
                    itemStack.getTag().putShort(nbtNode.node("key").getString(""), (short) nbtNode.node("value").getInt(0));
                    break;
                case "byte":
                    itemStack.getTag().putByte(nbtNode.node("key").getString(""), (byte) nbtNode.node("value").getInt(0));
                    break;
                case "long":
                    itemStack.getTag().putLong(nbtNode.node("key").getString(""), nbtNode.node("value").getLong(0));
                    break;
                default:
                case "string":
                    itemStack.getTag().putString(nbtNode.node("key").getString(""), nbtNode.node("value").getString(""));
                    break;
            }
        }

        return GuiFactory.displayableBuilder(ItemStack.class).itemStack(itemStack)
                .clickHandler((envyPlayer, clickType) ->
                        this.handleClick((EnvyPlayer<ServerPlayerEntity>) envyPlayer, ui, clickType,
                                commands.getOrDefault(clickType, Collections.emptyList()))).build();
    }

    private boolean canSee(ServerPlayerEntity player) {
        for (ItemRequirement requirement : this.requirements) {
            if (!requirement.fits(player)) {
                return false;
            }
        }

        return true;
    }

    private List<ITextComponent> getLore(ServerPlayerEntity player) {
        List<ITextComponent> translatedLore = Lists.newArrayList();

        for (String s : this.lore) {
            translatedLore.add(UtilChatColour.colour(
                    UtilPlaceholder.replaceIdentifiers(player, s)));
        }

        return translatedLore;
    }

    private void handleClick(EnvyPlayer<ServerPlayerEntity> player, GenericUI ui, Displayable.ClickType clickType,
                             List<String> commands) {
        List<String> remainingCommands = Lists.newArrayList();
        boolean closed = false;

        for (String command : commands) {
            if (command.startsWith("menu:")) {
                String menu = command.replace("menu: ", "");
                Menu nextMenu = MenusForge.getInstance().getMenu(menu);

                if (nextMenu == null) {
                    System.out.println("ERROR: cannot find menu `" + menu + "`");
                    continue;
                }

                ui.setAllowClose(true);
                player.getParent().closeScreen();
                closed = true;
                remainingCommands.add("player:menus open " + menu);
                continue;
            }

            if (command.equalsIgnoreCase("%close%")) {
                ui.setAllowClose(true);
                UtilForgeConcurrency.runSync(() -> player.getParent().closeScreen());
                closed = true;
                continue;
            }

            if (closed) {
                remainingCommands.add(command);
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

        if (closed) {
            UtilForgeConcurrency.runSync(() -> {
                for (String command : remainingCommands) {
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
            });
        }
    }
}
