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

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.events.HoloTouchEvent;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.protocol.netty.ChannelPipelineInjector;
import com.dsh105.holoapi.util.ReflectionUtil;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;
import com.google.common.collect.MapMaker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.ConcurrentMap;

public class InjectionManager {

    /**
     * The injection-strategy that will be used
     * to inject the player
     */
    protected InjectionStrategy strategy;

    /**
     * A "Cache" system for the injectors. (Eg: A reload will go slightly faster etc)
     * <p/>
     * Don't worry about concurrency, we use weak-keys so the GC'ing will go fine...
     */
    protected ConcurrentMap<Player, PlayerInjector> injectors = new MapMaker().weakKeys().makeMap();
    protected boolean closed;

    /**
     * The default constructor. The InjectionStrategy is the method that will
     * be used to inject our custom ProtocolHandler in the player obejct.
     * <p/>
     * (Used to detect packet IO)
     *
     * @param strategy
     */
    public InjectionManager(InjectionStrategy strategy) {
        this.strategy = strategy;
        this.closed = false; // Houston, we're up and running

        // Deal with any players that may be online due to a reload - seeing as lots of servers actually do this
        for (Player p : Bukkit.getOnlinePlayers()) {
            inject(p);
        }

        HoloAPI.getCore().getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                inject(event.getPlayer());
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        }, HoloAPI.getCore());
    }

    public InjectionStrategy getStrategy() {
        return this.strategy;
    }

    /**
     * Injects a specific player
     *
     * @param player
     */
    public void inject(Player player) {
        if (this.closed)
            return;

        if (injectors.containsKey(player)) {
            PlayerInjector injector = injectors.get(player);
            injector.setPlayer(player);
        } else {
            PlayerInjector playerInjector = this.strategy.inject(player, this);
            playerInjector.inject();
            injectors.put(player, playerInjector);
        }
    }

    /**
     * Un-injects a specific player
     *
     * @param player
     */
    public void unInject(Player player) {
        if (!injectors.containsKey(player)) {
            return;
        }

        PlayerInjector injector = injectors.get(player);

        if (injector.isInjected()) {
            injector.close();
        }
    }

    /**
     * Closes this InjectionManager
     */
    public void close() {
        for (Player player : injectors.keySet()) {
            unInject(player);
        }
        this.closed = true;
    }

    /**
     * Returns whether or not this InjectionManager is closed
     *
     * @return
     */
    public boolean isClosed() {
        return this.closed;
    }

    /**
     * Changes the "closed" state of this InjectionManager
     *
     * @param closed
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void handleIncomingPacket(ChannelPipelineInjector injector, final Player player, final Object msg) {
        if (!"PacketPlayInUseEntity".equals(msg.getClass().getSimpleName()))
            return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(HoloAPI.getCore(), new Runnable() {
            @Override
            public void run() {
                Packet packet = new Packet(msg);
                // The entity id of the hologram that got interacted with.
                int id = (Integer) packet.read("a");
                // The action is whether or not it was a right or left click.
                Action action = readAction(packet.read("action"));

                for (Hologram hologram : HoloAPI.getManager().getAllHolograms().keySet()) {
                    for (int entityId : hologram.getAllEntityIds()) {
                        if (id == entityId) {
                            for (TouchAction touchAction : hologram.getAllTouchActions()) {
                                HoloTouchEvent touchEvent = new HoloTouchEvent(hologram, player, touchAction, action);
                                HoloAPI.getCore().getServer().getPluginManager().callEvent(touchEvent);
                                if (!touchEvent.isCancelled()) {
                                    touchAction.onTouch(player, action);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private Action readAction(Object enumAction) {
        //return Action.getFromId((Integer) ReflectionUtil.invokeMethod(READ_ACTION, null, enumAction));
        return Action.getFromId((Integer) ReflectionUtil.getField(ReflectionUtil.getNMSClass("EnumEntityUseAction"), "d", enumAction));
    }
}
