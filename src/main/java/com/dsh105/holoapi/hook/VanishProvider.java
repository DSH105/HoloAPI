package com.dsh105.holoapi.hook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishPlugin;

public class VanishProvider extends PluginDependencyProvider<VanishPlugin> {

    public VanishProvider(Plugin myPluginInstance) {
        super(myPluginInstance, "VanishNoPacket");
    }

    @Override
    public void onHook() {

    }

    @Override
    public void onUnhook() {

    }

    public boolean isVanished(Player player) {
        return this.isVanished(player.getName());
    }

    public boolean isVanished(String player) {
        return this.isHooked() && this.getDependency().getManager().isVanished(player);
    }
}