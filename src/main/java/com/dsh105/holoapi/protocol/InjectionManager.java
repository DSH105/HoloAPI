package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.protocol.netty.PlayerInjector;
import com.google.common.collect.MapMaker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.ConcurrentMap;

public class InjectionManager {

    protected Plugin plugin;

    // We're using weak-keys here so don't worry about player instances not being GC'ed.
    protected static ConcurrentMap<Player, Injector> injections = new MapMaker().weakKeys().makeMap();

    private boolean isClosed = false;

    public InjectionManager(Plugin plugin) {
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be NULL!");

        this.plugin = plugin;
        this.isClosed = false;

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            inject(player);
        }

        plugin.getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onJoin(PlayerJoinEvent event) {
                inject(event.getPlayer());
            }
        }, plugin);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Injector getInjectorFor(Player player) {
        Injector injector = injections.get(player);
        if (injector == null) {
            injector = inject(player);

            if (injector == null)
                throw new RuntimeException("Failed to inject player: " + player);
        }

        return injector;
    }

    public Injector inject(Player player) {
        if (this.isClosed())
            return null;

        Injector injector = null;

        if (injections.containsKey(player)) {

            injector = injections.get(player);
            injector.setPlayer(player);

        } else {

            injector = new PlayerInjector(player, this);
            injector.inject();

            injections.put(player, injector);

        }

        return injector;
    }

    public void unInject(Player player) {
        if (getInjectorFor(player) == null)
            return;

        Injector injector = getInjectorFor(player);

        if (injector.isInjected())
            injector.close();
    }

    public void close() {
        if (isClosed())
            return;

        for (Player player : injections.keySet()) {
            unInject(player);
        }

        this.isClosed = true;
    }

    public boolean isClosed() {
        return this.isClosed;
    }
}
