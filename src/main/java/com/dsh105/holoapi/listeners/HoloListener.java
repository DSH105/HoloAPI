package com.dsh105.holoapi.listeners;

import com.dsh105.dshutils.util.GeometryUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.SimpleHoloManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class HoloListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        for (Hologram h : ((SimpleHoloManager) HoloAPI.getManager()).getAllHolograms().keySet()) {
            if (event.getTo().getWorld().getName().equals(h.getWorldName()) && GeometryUtil.getNearbyEntities(event.getTo(), 50).contains(player)) {
                if (h.getLocationFor(player) != null) {
                    h.show(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Hologram h : ((SimpleHoloManager) HoloAPI.getManager()).getAllHolograms().keySet()) {
            if (h.getLocationFor(player) != null) {
                h.clear(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Hologram h : ((SimpleHoloManager) HoloAPI.getManager()).getAllHolograms().keySet()) {
            if (player.getLocation().getWorld().getName().equals(h.getWorldName())) {
                h.show(player);
            }
        }
    }
}