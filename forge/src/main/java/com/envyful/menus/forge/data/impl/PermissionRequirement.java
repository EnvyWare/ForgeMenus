package com.envyful.menus.forge.data.impl;

import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.menus.forge.data.ItemRequirement;
import com.envyful.menus.forge.data.data.Requirement;
import net.minecraft.entity.player.EntityPlayerMP;

@Requirement("permission")
public class PermissionRequirement implements ItemRequirement {

    private final String requiredPermission;

    public static PermissionRequirement of(String permission) {
        return new PermissionRequirement(permission);
    }

    private PermissionRequirement(String requiredPermission) {this.requiredPermission = requiredPermission;}

    @Override
    public boolean fits(EntityPlayerMP player) {
        return UtilPlayer.hasPermission(player, this.requiredPermission);
    }
}
