package com.envyful.menus.forge.data.impl;

import com.envyful.api.forge.player.util.UtilPlayer;
import com.envyful.menus.forge.data.ItemRequirement;
import com.envyful.menus.forge.data.data.Requirement;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.configurate.ConfigurationNode;

@Requirement("permission")
public class PermissionRequirement implements ItemRequirement {

    private final String requiredPermission;

    public PermissionRequirement(ConfigurationNode node) {
        this.requiredPermission = node.node("permission").getString();
    }

    public PermissionRequirement(String permission) {
        this.requiredPermission = permission;
    }

    @Override
    public boolean fits(ServerPlayerEntity player) {
        return UtilPlayer.hasPermission(player, this.requiredPermission);
    }
}
