package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.action.TouchAction;
import com.dsh105.holoapi.api.event.HologramTouchEvent;
import com.dsh105.holoapi.util.ReflectionUtil;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;
import com.google.common.collect.MapMaker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

public class InjectionManager {

    protected HoloAPI holoAPI;

    protected ConcurrentMap<Player, ChannelPipelineInjector> injectors = new MapMaker().weakKeys().makeMap();

    public InjectionManager(final HoloAPI holoAPI) {
        if (holoAPI == null) {
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
                if (event.getPlugin().equals(holoAPI)) {
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
        if (injectors.containsKey(player)) {
            ChannelPipelineInjector injector = injectors.get(player);
            injector.setPlayer(player);
        } else {
            ChannelPipelineInjector pipelineInjector = new ChannelPipelineInjector(player, this);
            pipelineInjector.inject();
            injectors.put(player, pipelineInjector);
        }
    }

    public void unInject(Player player) {
        if (!injectors.containsKey(player)) {
            return;
        }

        ChannelPipelineInjector injector = injectors.get(player);

        if (injector.isInjected()) {
            injector.close();
        }
    }

    public void close() {
        for (Player player : injectors.keySet()) {
            unInject(player);
        }
    }

    private static final Method READ_ACTION = ReflectionUtil.getMethod(ReflectionUtil.getNMSClass("EnumEntityUseAction"), "a", ReflectionUtil.getNMSClass("EnumEntityUseAction"));

    public void handleIncomingPacket(ChannelPipelineInjector injector, final Player player, final Object msg) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.holoAPI, new Runnable() {
            @Override
            public void run() {
                if (!"PacketPlayInUseEntity".equals(msg.getClass().getName()))
                    return;
                Packet packet = new Packet(msg);
                // The entity id of the hologram that got interacted with.
                int id = (Integer) packet.read("a");
                // The action is whether or not it was a right or left click.
                Action action = readAction(packet.read("action"));

                for (Hologram hologram : HoloAPI.getManager().getAllHolograms().keySet()) {
                    for (int entityId : hologram.getAllEntityIds()) {
                        if (id == entityId) {
                            for (TouchAction touchAction : hologram.getAllTouchActions()) {
                                HologramTouchEvent touchEvent = new HologramTouchEvent(hologram, player, touchAction);
                                HoloAPI.getInstance().getServer().getPluginManager().callEvent(touchEvent);
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
        return Action.getFromId((Integer) ReflectionUtil.invokeMethod(READ_ACTION, null, enumAction));
    }
}
