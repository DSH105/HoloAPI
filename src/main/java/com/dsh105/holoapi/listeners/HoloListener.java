package com.dsh105.holoapi.listeners;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.util.GeometryUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HoloListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
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

    // TODO: Fix this
    // Currently overrides animations and messes with existing holograms
    // Also appears in `/holo info` list

    /*@EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            YAMLConfig config = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN);
            if (config.getBoolean("chatBubbles.show", false)) {
                final Player p = event.getPlayer();
                Location loc = p.getEyeLocation().clone();
                loc.add(0.0D, 0.5D, 0.0D);
                final int duration = config.getInt("chatBubbles.displayDurationSeconds", 8);
                final Hologram hologram = HoloAPI.getManager().createSimpleHologram(loc, duration, ChatColor.translateAlternateColorCodes('&', config.getString("chatBubbles.nameFormat", "&6&o")) + event.getPlayer().getName() + ":", ChatColor.WHITE + event.getMessage());

                if (config.getBoolean("chatBubbles.followPlayer", false)) {
                    class FollowPlayer extends BukkitRunnable {

                        private int i;
                        private Vector previous;

                        @Override
                        public void run() {
                            if (p == null || ++i >= (duration * 20)) {
                                this.cancel();
                            }
                            Location loc = p.getEyeLocation().clone();
                            if (previous == null
                                    || !(previous.getBlockX() == loc.getBlockX()
                                    && previous.getBlockY() == loc.getBlockY()
                                    && previous.getBlockZ() == loc.getBlockZ())) {
                                loc.add(0.0D, 0.5D, 0.0D);
                                hologram.move(loc);
                                previous = loc.toVector();
                            }
                        }
                    }

                    new FollowPlayer().runTaskTimer(HoloAPI.getInstance(), 1L, 1L);
                }
            }
        }
    }*/
}