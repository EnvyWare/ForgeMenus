package com.envyful.menus.forge.data.impl;

import com.envyful.menus.forge.data.ItemRequirement;
import com.envyful.menus.forge.data.data.Requirement;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

@Requirement("itemstack_in_slot")
public class ItemInSlotRequirement implements ItemRequirement {

    private final int slot;
    private final ItemStack itemStack;

    public static ItemInSlotRequirement of(int slot, ItemStack itemStack) {
        return new ItemInSlotRequirement(slot, itemStack);
    }

    protected ItemInSlotRequirement(int slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
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
