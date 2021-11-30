package com.envyful.menus.forge.data;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ItemRequirement {

    boolean fits(EntityPlayerMP player);

}
