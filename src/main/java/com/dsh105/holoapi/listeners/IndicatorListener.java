package com.dsh105.holoapi.listeners;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.util.RomanNumeral;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

public class IndicatorListener implements Listener {

    private static char HEART_CHARACTER = '\u2764';

    private YAMLConfig config;

    public IndicatorListener() {
        this.config = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.MAIN);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID && config.getBoolean("indicators.damage.enable", false)) {
            if (event.getEntity() instanceof Player && config.getBoolean("indicators.damage.showForPlayers", false)
                    || config.getBoolean("indicators.damage.showForMobs", false)) {
                if (!(event instanceof EntityDamageByEntityEvent)) {
                    String colours = config.getString("indicators.damage.format.default", "&c");

                    if (event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                        colours = config.getString("indicators.damage.format.drowning", "&b");
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                        colours = config.getString("indicators.damage.format.fire", "&4");
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.MAGIC) {
                        colours = config.getString("indicators.damage.format.magic", "&5");
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.POISON) {
                        colours = config.getString("indicators.damage.format.poison", "&2");
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
                        colours = config.getString("indicators.damage.format.starvation", "&6");
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
                        colours = config.getString("indicators.damage.format.thorns", "&e");
                    } else if (event.getCause() == EntityDamageEvent.DamageCause.WITHER) {
                        colours = config.getString("indicators.damage.format.wither", "&8");
                    }

                    String text = ChatColor.translateAlternateColorCodes('&', colours) + "-" + new DecimalFormat("#.0").format(event.getDamage()) + " " + HEART_CHARACTER;
                    HoloAPI.getManager().createSimpleHologram(event.getEntity().getLocation(), config.getInt("indicators.damage.timeVisible", 4), true, text);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID && config.getBoolean("indicators.damage.enable", false)) {
            if (event.getEntity() instanceof Player && config.getBoolean("indicators.damage.showForPlayers", true)
                    || config.getBoolean("indicators.damage.showForMobs", true)) {
                Vector v = event.getDamager().getLocation().toVector().subtract(event.getEntity().getLocation().toVector()).normalize().multiply((-0.012F) * event.getDamage());
                v.setY(v.getY() + 0.05D);
                HoloAPI.getManager().createSimpleHologram(event.getEntity().getLocation(), config.getInt("indicators.damage.timeVisible", 4), v, ChatColor.translateAlternateColorCodes('&', config.getString("indicators.damage.format.default", "&c")) + "-" + new DecimalFormat("#.0").format(event.getDamage()) + " " + HEART_CHARACTER);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpGain(PlayerExpChangeEvent event) {
        if (config.getBoolean("indicators.exp.enable", false)) {
            HoloAPI.getManager().createSimpleHologram(event.getPlayer().getLocation(), config.getInt("indicators.exp.timeVisible", 4), true, ChatColor.translateAlternateColorCodes('&', config.getString("indicators.exp.format", "&a")) + "+" + event.getAmount() + " exp");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (config.getBoolean("indicators.gainHealth.enable", false)) {
            if (event.getEntity() instanceof Player && config.getBoolean("indicators.gainHealth.showForPlayers", true)
                    || config.getBoolean("indicators.gainHealth.showForMobs", true)) {
                HoloAPI.getManager().createSimpleHologram(event.getEntity().getLocation(), config.getInt("indicators.gainHealth.timeVisible", 4), true, ChatColor.translateAlternateColorCodes('&', config.getString("indicators.gainHealth.format", "&a")) + "+" + event.getAmount() + " " + HEART_CHARACTER);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsumePotion(PlayerItemConsumeEvent event) {
        if (config.getBoolean("indicators.potion.enable", false)) {
            if (event.getItem().getType() == Material.POTION) {
                Potion potion = Potion.fromItemStack(event.getItem());
                if (potion != null) {
                    this.showPotionHologram(event.getPlayer(), potion.getEffects());
                }
            } else if (event.getItem().getType() == Material.GOLDEN_APPLE) {
                HoloAPI.getManager().createSimpleHologram(event.getPlayer().getLocation(), config.getInt("indicators.potion.timeVisible", 4), true, ChatColor.translateAlternateColorCodes('&', config.getString("indicators.potion.goldapple.format", "&e+ %effect%").replace("%effect%", "Golden Apple")));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSplashPotion(PotionSplashEvent event) {
        if (config.getBoolean("indicators.potion.enable", false)) {
            for (Entity e : event.getAffectedEntities()) {
                if (event.getEntity() instanceof Player && config.getBoolean("indicators.potion.showForPlayers", true)
                        || config.getBoolean("indicators.potion.showForMobs", true)) {
                    this.showPotionHologram(e, event.getPotion().getEffects());
                }
            }
        }
    }

    private void showPotionHologram(Entity e, Collection<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            //String type = StringUtil.capitalise(effect.getType().getName().replace("_", " "));
            int amp = effect.getAmplifier() < 1 ? 1 : effect.getAmplifier();
            String content = ChatColor.translateAlternateColorCodes('&', config.getString("indicators.potion.format." + effect.getType().getName().toLowerCase(), "&e+ %effect% %amp%"));
            content = content.replace("%effect%", StringUtil.capitalise(effect.getType().getName().replace("_", " "))).replace("%amp%", "" + new RomanNumeral(amp));
            HoloAPI.getManager().createSimpleHologram(e.getLocation(), config.getInt("indicators.potion.timeVisible", 4), true, content);
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
