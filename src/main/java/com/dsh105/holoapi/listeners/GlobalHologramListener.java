package com.dsh105.holoapi.listeners;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.SimpleHoloManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GlobalHologramListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        for (Hologram h : ((SimpleHoloManager) HoloAPI.getManager()).getAllHolograms().keySet()) {
            if (event.getTo().getWorld().getName().equals(h.getWorldName())) {
                if (h.getLocationFor(event.getPlayer()) != null) {
                    h.show(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        for (Hologram h : ((SimpleHoloManager) HoloAPI.getManager()).getAllHolograms().keySet()) {
            if (h.getLocationFor(event.getPlayer()) != null) {
                h.clear(event.getPlayer());
            }
        }
    }
}