package com.envyful.menus.forge.data.impl;

import com.envyful.api.config.util.UtilConfig;
import com.envyful.api.forge.config.UtilConfigItem;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.menus.forge.data.ItemRequirement;
import com.envyful.menus.forge.data.data.Requirement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

@Requirement("itemstack_in_inventory")
public class ItemInInventoryRequirement implements ItemRequirement {

    private final ItemStack itemStack;

    public ItemInInventoryRequirement(ConfigurationNode value) {
        Item itemType = UtilConfigItem.fromNameOrId(value.node("item_type").getString("minecraft:dirt"));
        int amount = value.node("amount").getInt(1);
        String name = value.node("name").getString();
        int damage = value.node("damage").getInt(0);
        List<String> lore = UtilConfig.getList(value, String.class, "lore");

        this.itemStack = new ItemBuilder()
                .type(itemType)
                .amount(amount)
                .name(name)
                .lore(lore)
                .build();
    }

    @Override
    public boolean fits(ServerPlayerEntity player) {
        int slot = player.inventory.getSlotFor(this.itemStack);
        return slot != -1;
    }
}
