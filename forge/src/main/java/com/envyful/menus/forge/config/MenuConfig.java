package com.envyful.menus.forge.config;

import com.google.common.collect.Lists;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;

public class MenuConfig extends Configurable {

    public static final String PATH = "Menus" + File.separator + "menus";

    public MenuConfig(String name) {
        super(PATH + File.separator + name);
    }

    @Override
    public void populate() throws SerializationException {
        this.getNode().node("inventory", "identifier").set("example_menu");
        this.getNode().node("inventory", "name").set("Example");
        this.getNode().node("inventory", "height").set(2);
        this.getNode().node("inventory", "close-commands").set(Lists.newArrayList(
                "console:msg %player% WELL DONE!",
                "console:msg %forge_name% Test",
                "player:help",
                "player:pay Envyful 1"
        ));
        this.getNode().node("inventory", "items", "one", "positionX").set(0);
        this.getNode().node("inventory", "items", "one", "positionY").set(0);
        this.getNode().node("inventory", "items", "one", "type").set(Item.getIdFromItem(Items.ACACIA_BOAT));
        this.getNode().node("inventory", "items", "one", "amount").set(1);
        this.getNode().node("inventory", "items", "one", "name").set("This is an item name! §bBLUE");
        this.getNode().node("inventory", "items", "one", "lore").set(Lists.newArrayList(
                "§bLore line 1!",
                "§bLore line 2!"
        ));
        this.getNode().node("inventory", "items", "one", "commands").set(Lists.newArrayList(
                "console:kill %forge_name%",
                "console:ban %forge_name% Test!"
        ));
    }
}
