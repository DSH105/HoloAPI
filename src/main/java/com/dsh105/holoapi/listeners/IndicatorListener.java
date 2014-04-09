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
import org.bukkit.entity.LivingEntity;
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
import java.util.HashMap;

public class IndicatorListener implements Listener {

    private static char HEART_CHARACTER = '\u2764';
    private static HashMap<String, ArrayList<String>> CHAT_BUBBLES = new HashMap<String, ArrayList<String>>();

    private YAMLConfig config;

    public IndicatorListener() {
        this.config = HoloAPI.getConfig(HoloAPI.ConfigType.MAIN);
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

                    String text = colours + "-" + new DecimalFormat("#.0").format(event.getDamage()) + " " + HEART_CHARACTER;
                    Location l = event.getEntity().getLocation().clone();
                    l.setY(l.getY() + config.getInt("indicators.damage.yOffset", 2));
                    HoloAPI.getManager().createSimpleHologram(l, config.getInt("indicators.damage.timeVisible", 4), true, text);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            if (livingEntity.getNoDamageTicks() > livingEntity.getMaximumNoDamageTicks() / 2.0F) {
                return;
            }
            if (event.getCause() != EntityDamageEvent.DamageCause.VOID && config.getBoolean("indicators.damage.enable", false)) {
                if (event.getEntity() instanceof Player && config.getBoolean("indicators.damage.showForPlayers", true)
                        || config.getBoolean("indicators.damage.showForMobs", true)) {
                    //Vector v = event.getDamager().getLocation().toVector().subtract(event.getEntity().getLocation().toVector()).normalize().multiply((-0.012F) * event.getDamage());
                    //v.setY(v.getY() + 0.05D);
                    Location l = event.getEntity().getLocation().clone();
                    l.setY(l.getY() + config.getInt("indicators.damage.yOffset", 2));
                    HoloAPI.getManager().createSimpleHologram(l, config.getInt("indicators.damage.timeVisible", 4), true, config.getString("indicators.damage.format.default", "&c") + "-" + new DecimalFormat("#.0").format(event.getDamage()) + " " + HEART_CHARACTER);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpGain(PlayerExpChangeEvent event) {
        if (config.getBoolean("indicators.exp.enable", false)) {
            Location l = event.getPlayer().getLocation().clone();
            l.setY(l.getY() + config.getInt("indicators.exp.yOffset", 2));
            HoloAPI.getManager().createSimpleHologram(l, config.getInt("indicators.exp.timeVisible", 4), true, config.getString("indicators.exp.format", "&a") + "+" + event.getAmount() + " exp");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (config.getBoolean("indicators.gainHealth.enable", false)) {
            if (event.getEntity() instanceof Player && config.getBoolean("indicators.gainHealth.showForPlayers", true)
                    || config.getBoolean("indicators.gainHealth.showForMobs", true)) {
                Location l = event.getEntity().getLocation().clone();
                l.setY(l.getY() + config.getInt("indicators.gainHealth.yOffset", 2));
                HoloAPI.getManager().createSimpleHologram(l, config.getInt("indicators.gainHealth.timeVisible", 4), true, config.getString("indicators.gainHealth.format", "&a") + "+" + event.getAmount() + " " + HEART_CHARACTER);
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
                String msg = config.getString("indicators.potion.format.goldenapple", "&e+ %effect%");
                if (event.getItem().getDurability() == 1) {
                    msg = config.getString("indicators.potion.format.godapple", "&e+ %effect%");
                }
                Location l = event.getPlayer().getLocation().clone();
                l.setY(l.getY() + config.getInt("indicators.potion.yOffset", 2));
                HoloAPI.getManager().createSimpleHologram(l, config.getInt("indicators.potion.timeVisible", 4), true, msg.replace("%effect%", "Golden Apple"));
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
            int amp = (effect.getAmplifier() < 1 ? 1 : effect.getAmplifier()) + 1;
            String content = config.getString("indicators.potion.format." + effect.getType().getName().toLowerCase(), "&e+ %effect% %amp%");
            content = content.replace("%effect%", StringUtil.capitalise(effect.getType().getName().replace("_", " "))).replace("%amp%", "" + new RomanNumeral(amp));
            Location l = e.getLocation().clone();
            l.setY(l.getY() + config.getInt("indicators.potion.yOffset", 2));
            HoloAPI.getManager().createSimpleHologram(l, config.getInt("indicators.potion.timeVisible", 4), true, content);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            final Player p = event.getPlayer();
            if (!HoloAPI.getVanishProvider().isVanished(p)) {
                final String msg = event.getMessage();
                if (event.isAsynchronous()) {
                    HoloAPI.getCore().getServer().getScheduler().scheduleSyncDelayedTask(HoloAPI.getCore(), new BukkitRunnable() {
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
    }

    private void showChatHologram(final Player p, String msg) {
        YAMLConfig config = HoloAPI.getConfig(HoloAPI.ConfigType.MAIN);
        if (config.getBoolean("chatBubbles.show", false)) {
            Location loc = p.getEyeLocation().clone();
            loc.add(0.0D, config.getDouble("chatBubbles.distanceAbovePlayerTag", 0.5D), 0.0D);
            final int duration = config.getInt("chatBubbles.displayDurationSeconds", 8);
            final boolean rise = config.getBoolean("chatBubbles.rise", true);
            final boolean followPlayer = config.getBoolean("chatBubbles.followPlayer", false);
            int charsPerLine = config.getInt("chatBubbles.charactersPerLine", 30);

            ArrayList<String> lines = new ArrayList<String>();
            if (config.getBoolean("chatBubbles.showPlayerName")) {
                lines.add(config.getString("chatBubbles.nameFormat", "&6&o") + p.getName() + ":");
            }
            int index = 0;
            while (index < msg.length()) {
                lines.add(ChatColor.WHITE + msg.substring(index, Math.min(index + charsPerLine, msg.length())));
                index += charsPerLine;
            }

            if (CHAT_BUBBLES.containsKey(p.getName())) {
                ArrayList<String> hologramIds = CHAT_BUBBLES.get(p.getName());
                if (!hologramIds.isEmpty()) {
                    Hologram last = null;
                    // Iterate from bottom to top
                    for (int j = hologramIds.size() - 1; j >= 0; j--) {
                        Hologram h = HoloAPI.getManager().getHologram(hologramIds.get(j));
                        if (h != null) {
                            double totalSize = (h.getLines().length * HoloAPI.getHologramLineSpacing());
                            double minY = h.getDefaultY() - totalSize;
                            if (last != null && minY < last.getDefaultY()) {
                                h.move(new Vector(h.getDefaultX(), h.getDefaultY() + (last.getLines().length * HoloAPI.getHologramLineSpacing()), h.getDefaultZ()));
                            } else {
                                if (minY < loc.getY()) {
                                    h.move(new Vector(h.getDefaultX(), h.getDefaultY() + totalSize, h.getDefaultZ()));
                                    last = h;
                                }
                            }
                        }
                    }
                }
            }

            final Hologram hologram = HoloAPI.getManager().createSimpleHologram(loc, duration, !followPlayer, lines);

            ArrayList<String> list;
            if (CHAT_BUBBLES.containsKey(p.getName())) {
                list = CHAT_BUBBLES.get(p.getName());
            } else {
                list = new ArrayList<String>();
            }

            list.add(hologram.getSaveId());
            CHAT_BUBBLES.put(p.getName(), list);
            new BukkitRunnable() {
                @Override
                public void run() {
                    ArrayList<String> list = CHAT_BUBBLES.get(p.getName());
                    if (list != null) {
                        list.remove(hologram.getSaveId());
                    }

                    CHAT_BUBBLES.put(p.getName(), list);
                }
            }.runTaskLater(HoloAPI.getCore(), duration * 20);

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

                new FollowPlayer().runTaskTimer(HoloAPI.getCore(), 1L, 1L);
            }
        }
    }
}
