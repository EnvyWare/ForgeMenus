package com.envyful.menus.forge.data;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface ItemRequirement {

    boolean fits(ServerPlayerEntity player);

}
