/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.holoapi.protocol;

import com.captainbern.hug.ClassHug;
import com.captainbern.minecraft.wrapper.EnumWrappers;
import com.captainbern.minecraft.wrapper.WrappedPacket;
import com.captainbern.reflection.ClassTemplate;
import com.captainbern.reflection.Reflection;
import com.dsh105.commodus.ServerUtil;
import com.dsh105.holoapi.protocol.netty.PlayerInjector;
import com.google.common.collect.MapMaker;
import org.bukkit.Bukkit;
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

    private ClassTemplate<Injector> injectorClass;

    public InjectionManager(Plugin plugin) {
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be NULL!");

        this.plugin = plugin;
        this.isClosed = false;

        for (Player player : ServerUtil.getOnlinePlayers()) {
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

        Injector injector;

        if (injections.containsKey(player)) {

            injector = injections.get(player);
            injector.setPlayer(player);

        } else {

            if (this.injectorClass == null) {
                if (Bukkit.getServer() == null)
                    throw new RuntimeException("Bukkit server not running!");

                String version = Bukkit.getServer().getVersion();
                ClassHug hug = new ClassHug("com.dsh105.holoapi.protocol.netty.PlayerInjector");

                if (version.contains("Cauldron") || version.contains("MCPC+")) {
                   hug.replace("net/minecraft/util/io/netty", "io/netty");
                }

                Class<?> changed = hug.giveAHug();
                this.injectorClass = (ClassTemplate<Injector>) new Reflection().reflect(changed);
            }


            injector = this.injectorClass.getSafeConstructor(Player.class, InjectionManager.class).getAccessor().invoke(player, this);
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

    public void handlePacket(WrappedPacket packet, PlayerInjector injector) {
        EnumWrappers.EntityUseAction useAction = packet.getEntityUseActions().read(0);

        // Fire Holo-touch with: useAction + injector.getPlayer();
    }
}
