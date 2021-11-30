package com.envyful.menus.forge.data.impl;

import com.envyful.api.config.util.UtilConfig;
import com.envyful.api.forge.items.ItemBuilder;
import com.envyful.menus.forge.data.ItemRequirement;
import com.envyful.menus.forge.data.data.Requirement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

@Requirement("itemstack_in_slot")
public class ItemInSlotRequirement implements ItemRequirement {

    private final int slot;
    private final ItemStack itemStack;

    public ItemInSlotRequirement(ConfigurationNode value) {
        Item itemType = Item.getByNameOrId(value.node("type").getString("minecraft:dirt"));
        int amount = value.node("amount").getInt(1);
        String name = value.node("name").getString();
        int damage = value.node("damage").getInt(0);
        List<String> lore = UtilConfig.getList(value, String.class, "lore");

        this.slot = value.node("slot").getInt();
        this.itemStack = new ItemBuilder()
                .type(itemType)
                .amount(amount)
                .name(name)
                .damage(damage)
                .lore(lore)
                .build();
    }

    @Override
    public boolean fits(EntityPlayerMP player) {
        ItemStack stackInSlot = player.inventory.getStackInSlot(this.slot);

        if (stackInSlot == null || stackInSlot.isEmpty()) {
            return false;
        }

        return stackInSlot.isItemEqual(this.itemStack);
    }
}
