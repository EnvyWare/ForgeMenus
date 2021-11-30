package com.envyful.menus.forge.data;

import com.envyful.api.config.util.UtilConfig;
import com.envyful.api.forge.chat.UtilChatColour;
import com.envyful.api.forge.concurrency.UtilForgeConcurrency;
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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public class ConfigItem {

    private final Item itemType;
    private final int amount;
    private final String name;
    private final int damage;
    private final String tooltip;
    private final List<String> lore;
    private final List<String> commands;
    private final List<ItemRequirement> requirements;
    private final ConfigItem elseItem;
    private final ConfigurationNode node;

    public ConfigItem(ConfigurationNode value) {
        this.itemType = Item.getByNameOrId(value.node("type").getString("minecraft:dirt"));
        this.amount = value.node("amount").getInt(1);
        this.name = value.node("name").getString();
        this.damage = value.node("damage").getInt(0);
        this.tooltip = value.node("tooltip").getString("");
        this.lore = UtilConfig.getList(value, String.class, "lore");
        this.commands = UtilConfig.getList(value, String.class, "commands");
        this.requirements = Lists.newArrayList();

        if (value.hasChild("requirement")) {
            this.requirements.add(new PermissionRequirement(value));
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

    public Displayable build(EntityPlayerMP player, GenericUI ui) {
        if (!this.requirements.isEmpty() && !this.canSee(player)) {
            return this.elseItem.build(player, ui);
        }

        ItemBuilder builder = new ItemBuilder()
                .type(itemType == null ? Items.SKULL : itemType)
                .amount(amount)
                .name(UtilChatColour.translateColourCodes('&', UtilPlaceholder.replaceIdentifiers(player, this.name)))
                .lore(this.getLore(player))
                .damage(damage);

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
                itemStack.getOrCreateSubCompound("SkullOwner").setString("textures", base64);
            } else if (this.node.node("type").getString().equals("playerhead")) {
                itemStack.getTagCompound().setString("SkullOwner", player.getName());
            }
        }

        itemStack.getOrCreateSubCompound("UnsafeData").setString("tooltip", tooltip);

        for (ConfigurationNode nbtNode : this.node.node("nbt").childrenMap().values()) {
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
                default:
                case "string":
                    itemStack.getTagCompound().setString(nbtNode.node("key").getString(""), nbtNode.node("value").getString(""));
                    break;
            }
        }

        return GuiFactory.displayableBuilder(ItemStack.class).itemStack(itemStack)
                .clickHandler((envyPlayer, clickType) ->
                        this.handleClick((EnvyPlayer<EntityPlayerMP>) envyPlayer, ui, clickType, commands)).build();
    }

    private boolean canSee(EntityPlayerMP player) {
        for (ItemRequirement requirement : this.requirements) {
            if (!requirement.fits(player)) {
                return false;
            }
        }

        return true;
    }

    private List<String> getLore(EntityPlayerMP player) {
        List<String> translatedLore = Lists.newArrayList();

        for (String s : this.lore) {
            translatedLore.add(UtilChatColour.translateColourCodes('&',
                    UtilPlaceholder.replaceIdentifiers(player, s)));
        }

        return translatedLore;
    }

    private void handleClick(EnvyPlayer<EntityPlayerMP> player, GenericUI ui, Displayable.ClickType clickType,
                             List<String> commands) {
        if (ui.isClicked()) {
            return;
        }

        ui.setClicked(true);
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
                player.getParent().closeScreen();
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
