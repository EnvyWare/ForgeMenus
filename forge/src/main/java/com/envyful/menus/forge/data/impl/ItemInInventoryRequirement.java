package com.envyful.menus.forge.data.impl;

import com.envyful.menus.forge.data.ItemRequirement;
import com.envyful.menus.forge.data.data.Requirement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

@Requirement("itemstack_in_inventory")
public class ItemInInventoryRequirement implements ItemRequirement {

    private final ItemStack itemStack;

    public static ItemInInventoryRequirement of(ItemStack itemStack) {
        return new ItemInInventoryRequirement(itemStack);
    }

    protected ItemInInventoryRequirement(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean fits(EntityPlayerMP player) {
        int slot = player.inventory.getSlotFor(this.itemStack);
        return slot != -1;
    }
}
