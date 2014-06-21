package com.dsh105.holoapi.protocol2;

import org.bukkit.plugin.Plugin;

public class InjectionManager {

    protected Plugin plugin;

    public InjectionManager(Plugin plugin) {
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be NULL!");

        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}
