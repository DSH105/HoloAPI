package com.dsh105.holoapi.listeners;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.holoapi.HoloAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.util.Vector;

public class IndicatorListener implements Listener {

    private YAMLConfig config;

    public IndicatorListener() {
        this.config = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (config.getBoolean("indicators.damage.enable", false)) {
            if (event.getEntity() instanceof Player && config.getBoolean("indicators.damage.showForPlayers", false)
                    || config.getBoolean("indicators.damage.showForMobs", false)) {
                if (!(event instanceof EntityDamageByEntityEvent)) {
                    HoloAPI.getManager().createSimpleHologram(event.getEntity().getLocation(), config.getInt("indicators.damage.timeVisible", 4), true, ChatColor.RED + "-" + event.getDamage() + " \u2764");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (config.getBoolean("indicators.damage.enable", false)) {
            if (event.getEntity() instanceof Player && config.getBoolean("indicators.damage.showForPlayers", false)
                    || config.getBoolean("indicators.damage.showForMobs", false)) {
                Vector v = event.getDamager().getLocation().toVector().subtract(event.getEntity().getLocation().toVector()).normalize().multiply((-0.012F) * event.getDamage());
                v.setY(v.getY() + 0.05D);
                HoloAPI.getManager().createSimpleHologram(event.getEntity().getLocation(), config.getInt("indicators.damage.timeVisible", 4), v, ChatColor.RED + "-" + event.getDamage() + " \u2764");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onExpGain(PlayerExpChangeEvent event) {
        if (config.getBoolean("indicators.exp.enable", false)) {
            HoloAPI.getManager().createSimpleHologram(event.getPlayer().getLocation(), config.getInt("indicators.exp.timeVisible", 4), true, ChatColor.GREEN + "+" + event.getAmount() + " exp");
        }
    }
}