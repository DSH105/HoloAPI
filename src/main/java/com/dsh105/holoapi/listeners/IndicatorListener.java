package com.dsh105.holoapi.listeners;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class IndicatorListener implements Listener {

    private YAMLConfig config;

    public IndicatorListener() {
        this.config = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (config.getBoolean("indicators.damage.enable", false)) {
            if (event.getEntity() instanceof Player && config.getBoolean("indicators.damage.showForPlayers", false)
                    || config.getBoolean("indicators.damage.showForMobs", false)) {
                if (!(event instanceof EntityDamageByEntityEvent)) {
                    HoloAPI.getManager().createSimpleHologram(event.getEntity().getLocation(), config.getInt("indicators.damage.timeVisible", 4), true, ChatColor.translateAlternateColorCodes('&', config.getString("indicators.damage.format", "&c")) + "-" + event.getDamage() + " \u2764");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (config.getBoolean("indicators.damage.enable", false)) {
            if (event.getEntity() instanceof Player && config.getBoolean("indicators.damage.showForPlayers", false)
                    || config.getBoolean("indicators.damage.showForMobs", false)) {
                Vector v = event.getDamager().getLocation().toVector().subtract(event.getEntity().getLocation().toVector()).normalize().multiply((-0.012F) * event.getDamage());
                v.setY(v.getY() + 0.05D);
                HoloAPI.getManager().createSimpleHologram(event.getEntity().getLocation(), config.getInt("indicators.damage.timeVisible", 4), v, ChatColor.translateAlternateColorCodes('&', config.getString("indicators.damage.format", "&c")) + "-" + event.getDamage() + " \u2764");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpGain(PlayerExpChangeEvent event) {
        if (config.getBoolean("indicators.exp.enable", false)) {
            HoloAPI.getManager().createSimpleHologram(event.getPlayer().getLocation(), config.getInt("indicators.exp.timeVisible", 4), true, ChatColor.translateAlternateColorCodes('&', config.getString("indicators.exp.format", "&a")) + "+" + event.getAmount() + " exp");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            final Player p = event.getPlayer();
            final String msg = event.getMessage();
            if (event.isAsynchronous()) {
                HoloAPI.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(HoloAPI.getInstance(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        showChatHologram(p, msg);
                    }
                });
            } else {
                this.showChatHologram(p, msg);
            }
        }
    }

    private void showChatHologram(final Player p, String msg) {
        YAMLConfig config = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN);
        if (config.getBoolean("chatBubbles.show", false)) {
            Location loc = p.getEyeLocation().clone();
            loc.add(0.0D, 0.5D, 0.0D);
            final int duration = config.getInt("chatBubbles.displayDurationSeconds", 8);
            final boolean rise = config.getBoolean("chatBubbles.rise", true);
            final boolean followPlayer = config.getBoolean("chatBubbles.followPlayer", false);
            int charsPerLine = config.getInt("chatBubbles.charactersPerLine", 30);

            ArrayList<String> lines = new ArrayList<String>();
            if (config.getBoolean("chatBubbles.showPlayerName")) {
                lines.add(ChatColor.translateAlternateColorCodes('&', config.getString("chatBubbles.nameFormat", "&6&o")) + p.getName() + ":");
            }
            int index = 0;
            while (index < msg.length()) {
                lines.add(ChatColor.WHITE + msg.substring(index, Math.min(index + charsPerLine, msg.length())));
                index += charsPerLine;
            }

            final Hologram hologram = HoloAPI.getManager().createSimpleHologram(loc, duration, !followPlayer, lines);

            if (followPlayer) {
                class FollowPlayer extends BukkitRunnable {

                    private int i;
                    private double riseDiff = 0.0D;

                    @Override
                    public void run() {
                        if (p == null || ++i >= ((duration * 20) - 1)) {
                            this.cancel();
                        }
                        Location l = p.getEyeLocation();
                        if (rise) {
                            riseDiff += 0.02D;
                        }
                        hologram.move(new Vector(l.getX(), l.getY() + 0.5D + riseDiff, l.getZ()));
                    }
                }

                new FollowPlayer().runTaskTimer(HoloAPI.getInstance(), 1L, 1L);
            }
        }
    }
}