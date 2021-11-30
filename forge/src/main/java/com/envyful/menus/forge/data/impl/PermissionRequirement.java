package com.envyful.menus.forge.data.impl;

import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.menus.forge.data.ItemRequirement;
import com.envyful.menus.forge.data.data.Requirement;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.configurate.ConfigurationNode;

@Requirement("permission")
public class PermissionRequirement implements ItemRequirement {

    private final String requiredPermission;

    public PermissionRequirement(ConfigurationNode node) {
        this.requiredPermission = node.node("permission").getString();
    }

    @Override
    public boolean fits(EntityPlayerMP player) {
        return UtilPlayer.hasPermission(player, this.requiredPermission);
    }
}
