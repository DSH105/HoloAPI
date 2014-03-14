package com.dsh105.holoapi.listeners;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class HoloListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
            if (!h.isSimple()) {
                if (event.getTo().getWorld().getName().equals(h.getWorldName())) {
                    if (h.getLocationFor(player) != null) {
                        h.show(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
            if (h.getLocationFor(player) != null) {
                h.clear(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        for (final Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
            if (!h.isSimple()) {
                if (player.getLocation().getWorld().getName().equals(h.getWorldName())) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (h instanceof AnimatedHologram && !((AnimatedHologram) h).isAnimating()) {
                                ((AnimatedHologram) h).animate();
                            }
                            h.show(player);
                        }
                    }.runTaskLater(HoloAPI.getInstance(), 40L);
                }
            }
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        for (final Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
            if (player.getLocation().getWorld().getName().equals(h.getWorldName())) {
                if (!h.isSimple()) {
                    if (h instanceof AnimatedHologram && !((AnimatedHologram) h).isAnimating()) {
                        ((AnimatedHologram) h).animate();
                    }
                    h.show(player);
                }
            } else if (event.getFrom().getName().equals(h.getWorldName()) && h.getLocationFor(player) != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        h.clear(player);
                    }
                }.runTaskLater(HoloAPI.getInstance(), 20L);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
            if (h.getDefaultLocation().getChunk().equals(event.getChunk())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
            if (h.getDefaultLocation().getChunk().equals(event.getChunk())) {
                for (Entity e : h.getDefaultLocation().getWorld().getEntities()) {
                    if (e instanceof Player) {
                        h.show((Player) e);
                    }
                }
            }
        }
    }
}
