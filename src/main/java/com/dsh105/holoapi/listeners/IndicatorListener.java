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

import com.dsh105.commodus.RomanNumeral;
import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import java.util.*;

public class IndicatorListener implements Listener {

    private static final char HEART_CHARACTER = '\u2764';
    private static final DecimalFormat DAMAGE_FORMAT = new DecimalFormat("#.0");
    private static final HashMap<String, ArrayList<String>> CHAT_BUBBLES = new HashMap<>();
    private static final List<EntityDamageEvent.DamageCause> SUPPORTED_DAMAGE_TYPES = Arrays.asList(EntityDamageEvent.DamageCause.DROWNING, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.MAGIC, EntityDamageEvent.DamageCause.POISON, EntityDamageEvent.DamageCause.THORNS, EntityDamageEvent.DamageCause.WITHER);

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {

        // Check if damage indicators are enabled
        if (!Settings.INDICATOR_ENABLE.getValue("damage")) {
            return;
        }

        // Don't show damage indicators for void damage
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            return;
        }

        // Make sure that indicators are enabled for this entity type
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (!Settings.INDICATOR_SHOW_FOR_PLAYERS.getValue("damage")) {
                return;
            }
        } else if (event.getEntity() instanceof LivingEntity) {
            if (!Settings.INDICATOR_SHOW_FOR_MOBS.getValue("damage")) {
                return;
            }
        } else {
            return; // We only show indicators for players and mobs.
        }

        final LivingEntity entity = (LivingEntity) event.getEntity();
        if (entity.getNoDamageTicks() > entity.getMaximumNoDamageTicks() / 2.0F) {
            return;
        }


        String damagePrefix = Settings.INDICATOR_FORMAT.getValue("damage", "default");

        // Get our DamageCause-specific damagePrefix, if any
        if (SUPPORTED_DAMAGE_TYPES.contains(event.getCause())) {
            String type = event.getCause().toString().toLowerCase();
            if (event.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                type = "fire";
            }
            damagePrefix = Settings.INDICATOR_FORMAT.getValue("damage", type);
            if (!Settings.INDICATOR_ENABLE_TYPE.getValue("damage", type)) {
                return; // This type of indicator is disabled
            }
        }


        // Build the message prefix and suffix (i.e. the portions without the damage)
        final String indicatorPrefix = damagePrefix + "-";
        final String indicatorSuffix = " " + HEART_CHARACTER;

        final double healthBefore = entity.getHealth();
        Bukkit.getScheduler().runTask(HoloAPI.getCore(), new Runnable() {
            @Override
            public void run() {
                double damageTaken = healthBefore - entity.getHealth();
                if (damageTaken > 0) {
                    // Round to the nearest .5
                    damageTaken = Math.round(damageTaken * 2.0D) / 2.0D;

                    String text = indicatorPrefix + DAMAGE_FORMAT.format(damageTaken) + indicatorSuffix;
                    Location loc = entity.getLocation();
                    loc.setY(loc.getY() + Settings.INDICATOR_Y_OFFSET.getValue("damage"));
                    HoloAPI.getManager().createSimpleHologram(loc, Settings.INDICATOR_TIME_VISIBLE.getValue("damage"), true, text);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onExpGain(PlayerExpChangeEvent event) {
        if (Settings.INDICATOR_ENABLE.getValue("exp")) {
            Location l = event.getPlayer().getLocation().clone();
            l.setY(l.getY() + Settings.INDICATOR_Y_OFFSET.getValue("exp"));
            HoloAPI.getManager().createSimpleHologram(l, Settings.INDICATOR_TIME_VISIBLE.getValue("exp"), true, Settings.INDICATOR_FORMAT.getValue("exp", "default") + "+" + event.getAmount() + " exp");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (Settings.INDICATOR_ENABLE.getValue("gainHealth")) {
            if ((event.getEntity() instanceof Player && Settings.INDICATOR_SHOW_FOR_PLAYERS.getValue("gainHealth")) || Settings.INDICATOR_SHOW_FOR_MOBS.getValue("gainHealth")) {
                Location l = event.getEntity().getLocation().clone();
                l.setY(l.getY() + Settings.INDICATOR_Y_OFFSET.getValue("gainHealth"));
                HoloAPI.getManager().createSimpleHologram(l, Settings.INDICATOR_TIME_VISIBLE.getValue("gainHealth"), true, Settings.INDICATOR_FORMAT.getValue("gainHealth", "default") + "+" + event.getAmount() + " " + HEART_CHARACTER);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsumePotion(PlayerItemConsumeEvent event) {
        if (Settings.INDICATOR_ENABLE.getValue("potion")) {
            if (event.getItem().getType() == Material.POTION) {
                Potion potion = Potion.fromItemStack(event.getItem());
                if (potion != null) {
                    this.showPotionHologram(event.getPlayer(), potion.getEffects());
                }
            } else if (event.getItem().getType() == Material.GOLDEN_APPLE) {
                String msg = Settings.INDICATOR_FORMAT.getValue("potion", "goldenapple");
                if (event.getItem().getDurability() == 1) {
                    msg = Settings.INDICATOR_FORMAT.getValue("potion", "godapple");
                }
                Location l = event.getPlayer().getLocation().clone();
                l.setY(l.getY() + Settings.INDICATOR_Y_OFFSET.getValue("potion"));
                HoloAPI.getManager().createSimpleHologram(l, Settings.INDICATOR_TIME_VISIBLE.getValue("potion"), true, msg.replace("%effect%", "Golden Apple"));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSplashPotion(PotionSplashEvent event) {
        if (Settings.INDICATOR_ENABLE.getValue("potion")) {
            for (Entity e : event.getAffectedEntities()) {
                if ((event.getEntity() instanceof Player && Settings.INDICATOR_SHOW_FOR_PLAYERS.getValue("potion")) || Settings.INDICATOR_SHOW_FOR_MOBS.getValue("potion")) {
                    this.showPotionHologram(e, event.getPotion().getEffects());
                }
            }
        }
    }

    private void showPotionHologram(Entity e, Collection<PotionEffect> effects) {
        for (PotionEffect effect : effects) {
            int amp = (effect.getAmplifier() < 1 ? 1 : effect.getAmplifier()) + 1;
            String content = Settings.INDICATOR_FORMAT.getValue("potion", effect.getType().getName().toLowerCase());
            content = content.replace("%effect%", StringUtil.capitalise(effect.getType().getName().replace("_", " "))).replace("%amp%", "" + new RomanNumeral(amp));
            Location l = e.getLocation().clone();
            l.setY(l.getY() + Settings.INDICATOR_Y_OFFSET.getValue("potion"));
            HoloAPI.getManager().createSimpleHologram(l, Settings.INDICATOR_TIME_VISIBLE.getValue("potion"), true, content);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
        //YAMLConfig config = HoloAPI.getConfig(HoloAPI.ConfigType.MAIN);
        if (Settings.CHATBUBBLES_SHOW.getValue()) {
            Location loc = p.getEyeLocation().clone();
            loc.add(0D, Settings.CHATBUBBLES_DISTANCE_ABOVE.getValue(), 0D);
            final int duration = Settings.CHATBUBBLES_DISPLAY_DURATION.getValue();
            final boolean rise = Settings.CHATBUBBLES_RISE.getValue();
            final boolean followPlayer = Settings.CHATBUBBLES_FOLLOW_PLAYER.getValue();
            int charsPerLine = Settings.CHATBUBBLES_CHARACTERS_PER_LINE.getValue();

            ArrayList<String> lines = new ArrayList<>();
            if (Settings.CHATBUBBLES_SHOW_PLAYER_NAME.getValue()) {
                lines.add(Settings.CHATBUBBLES_NAME_FORMAT.getValue() + p.getName() + ":");
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
                            double totalSize = (h.getLines().length * Settings.VERTICAL_LINE_SPACING.getValue());
                            double minY = h.getDefaultY() - totalSize;
                            if (last != null && minY < last.getDefaultY()) {
                                h.move(new Vector(h.getDefaultX(), h.getDefaultY() + (last.getLines().length * Settings.VERTICAL_LINE_SPACING.getValue()), h.getDefaultZ()));
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
                list = new ArrayList<>();
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

                new FollowPlayer().runTaskTimer(HoloAPI.getCore(), 10L, 10L);
            }
        }
    }
}
