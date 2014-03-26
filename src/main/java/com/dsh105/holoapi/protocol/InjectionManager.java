package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.HoloAPI;
import com.google.common.collect.MapMaker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.concurrent.ConcurrentMap;

public class InjectionManager {

    protected HoloAPI holoAPI;

    protected ConcurrentMap<Player, ChannelPipelineInjector> injectors = new MapMaker().weakKeys().makeMap();

    public InjectionManager(final HoloAPI holoAPI) {
        if(holoAPI == null) {
            throw new IllegalArgumentException("Provided HoloAPI instance can't be NULL!");
        }

        this.holoAPI = holoAPI;

        holoAPI.getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                inject(event.getPlayer());
            }

            @EventHandler
            public void onDisable(PluginDisableEvent event) {
                if(event.getPlugin().equals(holoAPI)) {
                    close();
                }
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        }, holoAPI);
    }

    public void inject(Player player) {
        if(injectors.containsKey(player)) {
            ChannelPipelineInjector injector = injectors.get(player);
            injector.setPlayer(player);
        } else {
            ChannelPipelineInjector pipelineInjector = new ChannelPipelineInjector(player, this);
            pipelineInjector.inject();
            injectors.put(player, pipelineInjector);
        }
    }

    public void unInject(Player player) {
        if(!injectors.containsKey(player)) {
            return;
        }

        ChannelPipelineInjector injector = injectors.get(player);

        if(injector.isInjected()) {
            injector.close();
        }
    }

    public void close() {
        for(Player player : injectors.keySet()) {
            unInject(player);
        }
    }

    public Object handleIncomingPacket(ChannelPipelineInjector injector, Player player, Object packet) {
        // TODO: handle the packet, make sure to do this on the main thread
        return null;
    }
}
