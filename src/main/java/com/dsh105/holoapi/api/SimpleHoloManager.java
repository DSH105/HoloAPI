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

package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.HoloAPICore;
import com.dsh105.holoapi.api.events.*;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.config.YAMLConfig;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.StringUtil;
import com.dsh105.holoapi.util.TagIdGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Level;

public class SimpleHoloManager implements HoloManager {

    YAMLConfig config;
    private UpdateDisplayTask updateDisplayTask;
    private HashMap<Hologram, Plugin> holograms = new HashMap<Hologram, Plugin>();

    public SimpleHoloManager() {
        this.config = HoloAPI.getConfig(HoloAPI.ConfigType.DATA);
        this.updateDisplayTask = new UpdateDisplayTask();
    }

    @Override
    public HashMap<Hologram, Plugin> getAllHolograms() {
        HashMap<Hologram, Plugin> map = new HashMap<Hologram, Plugin>();
        map.putAll(this.holograms);
        return map;
    }

    @Override
    public HashMap<Hologram, Plugin> getAllComplexHolograms() {
        HashMap<Hologram, Plugin> map = new HashMap<Hologram, Plugin>();
        for (Map.Entry<Hologram, Plugin> entry : this.holograms.entrySet()) {
            if (!entry.getKey().isSimple()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    @Override
    public HashMap<Hologram, Plugin> getAllSimpleHolograms() {
        HashMap<Hologram, Plugin> map = new HashMap<Hologram, Plugin>();
        for (Map.Entry<Hologram, Plugin> entry : this.holograms.entrySet()) {
            if (entry.getKey().isSimple()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public void clearAll() {
        Iterator<Hologram> i = holograms.keySet().iterator();
        while (i.hasNext()) {
            Hologram h = i.next();
            this.saveToFile(h);
            h.clearAllPlayerViews();
            i.remove();
        }
    }

    @Override
    public ArrayList<Hologram> getHologramsFor(Plugin owningPlugin) {
        ArrayList<Hologram> list = new ArrayList<Hologram>();
        for (Map.Entry<Hologram, Plugin> entry : this.holograms.entrySet()) {
            if (entry.getValue().equals(owningPlugin)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    @Override
    public Hologram getHologram(String hologramId) {
        for (Hologram hologram : this.holograms.keySet()) {
            if (hologram.getSaveId().equals(hologramId)) {
                return hologram;
            }
        }
        return null;
    }

    @Override
    public void track(Hologram hologram, Plugin owningPlugin) {
        this.holograms.put(hologram, owningPlugin);
        if (!hologram.isSimple() && this.config.getConfigurationSection("holograms." + hologram.getSaveId()) == null) {
            this.saveToFile(hologram);
        }
        if (hologram instanceof AnimatedHologram && !((AnimatedHologram) hologram).isAnimating()) {
            ((AnimatedHologram) hologram).animate();
        }
        HoloAPI.getCore().getServer().getPluginManager().callEvent(new HoloCreateEvent(hologram));

        for (String s : hologram.getLines()) {
            if (s.contains(HoloAPI.getConfig(HoloAPI.ConfigType.MAIN).getString("multicolorFormat.character", "&s"))) {
                MultiColourFormat.CACHE.add(hologram);
            }
        }
    }

    @Override
    public void stopTracking(Hologram hologram) {
        hologram.clearAllPlayerViews();
        this.holograms.remove(hologram);
        if (hologram instanceof AnimatedHologram && ((AnimatedHologram) hologram).isAnimating()) {
            ((AnimatedHologram) hologram).cancelAnimation();
        }
        HoloAPI.getCore().getServer().getPluginManager().callEvent(new HoloDeleteEvent(hologram));
        //this.clearFromFile(hologram);

        if (MultiColourFormat.CACHE.contains(hologram)) {
            MultiColourFormat.CACHE.remove(hologram);
        }
    }

    @Override
    public void stopTracking(String hologramId) {
        Hologram hologram = this.getHologram(hologramId);
        if (hologram != null) {
            this.stopTracking(hologram);
        }
    }

    @Override
    public void saveToFile(String hologramId) {
        Hologram hologram = this.getHologram(hologramId);
        if (hologram != null) {
            this.saveToFile(hologram);
        }
    }

    @Override
    public void saveToFile(Hologram hologram) {
        if (!hologram.isSimple()) {
            String path = "holograms." + hologram.getSaveId() + ".";
            this.config.set(path + "worldName", hologram.getWorldName());
            this.config.set(path + "x", hologram.getDefaultX());
            this.config.set(path + "y", hologram.getDefaultY());
            this.config.set(path + "z", hologram.getDefaultZ());
            if (hologram instanceof AnimatedHologram) {
                AnimatedHologram animatedHologram = (AnimatedHologram) hologram;
                if (animatedHologram.isImageGenerated() && (HoloAPI.getAnimationLoader().exists(animatedHologram.getAnimationKey())) || HoloAPI.getAnimationLoader().existsAsUnloadedUrl(animatedHologram.getAnimationKey())) {
                    this.config.set(path + "animatedImage.image", true);
                    this.config.set(path + "animatedImage.key", animatedHologram.getAnimationKey());
                } else {
                    this.config.set(path + "animatedImage.image", false);
                    int index = 0;
                    for (Frame f : animatedHologram.getFrames()) {
                        this.config.set(path + "animatedImage.frames." + index + ".delay", f.getDelay());
                        int tagIndex = 0;
                        for (String tag : f.getLines()) {
                            this.config.set(path + "animatedImage.frames." + index + "." + tagIndex, tag.replace(ChatColor.COLOR_CHAR, '&'));
                            tagIndex++;
                        }
                        index++;
                    }
                }
            } else {
                int index = 0;
                for (StoredTag tag : hologram.serialise()) {
                    this.config.set(path + "lines." + index + ".type", tag.isImage() ? "image" : "text");
                    this.config.set(path + "lines." + index + ".value", tag.getContent().replace(ChatColor.COLOR_CHAR, '&'));
                    index++;
                }
            }
            for (TouchAction touch : hologram.getAllTouchActions()) {
                if (touch.getSaveKey() != null) {
                    Map<String, Object> map = touch.getDataToSave();
                    if (map != null && !map.isEmpty()) {
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            // Let the developer implementing the API handle how data is saved and loaded to and from holograms
                            this.config.set(path + "touchactions." + touch.getSaveKey() + "." + entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
            Visibility visibility = hologram.getVisibility();
            if (visibility != null && visibility.getSaveKey() != null) {
                Map<String, Object> map = visibility.getDataToSave();
                if (map != null && !map.isEmpty()) {
                    this.config.set(path + "visibility", null);
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        // Let the developer implementing the API handle how data is saved and loaded to and from holograms
                        this.config.set(path + "visibility." + visibility.getSaveKey() + "." + entry.getKey(), entry.getValue());
                    }
                }
            }
            this.config.saveConfig();
        }
    }

    @Override
    public void clearFromFile(String hologramId) {
        this.config.set("holograms." + hologramId + "", null);
        this.config.saveConfig();
    }

    @Override
    public void clearFromFile(Hologram hologram) {
        this.clearFromFile(hologram.getSaveId());
    }

    public ArrayList<String> loadFileData() {
        ArrayList<String> unprepared = new ArrayList<String>();
        ConfigurationSection cs = config.getConfigurationSection("holograms");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                String path = "holograms." + key + ".";
                String worldName = config.getString(path + "worldName");
                double x = config.getDouble(path + "x");
                double y = config.getDouble(path + "y");
                double z = config.getDouble(path + "z");
                if (config.get(path + "animatedImage.image") != null) {
                    if (config.getBoolean(path + "animatedImage.image")) {
                        unprepared.add(key);
                    } else {
                        ArrayList<Frame> frameList = new ArrayList<Frame>();
                        ConfigurationSection frames = config.getConfigurationSection("holograms." + key + ".animatedImage.frames");
                        if (frames != null) {
                            for (String frameKey : frames.getKeys(false)) {
                                ConfigurationSection lines = config.getConfigurationSection("holograms." + key + ".animatedImage.frames." + frameKey);
                                if (lines != null) {
                                    ArrayList<String> tagList = new ArrayList<String>();
                                    int delay = config.getInt("holograms." + key + ".animatedImage.frames." + frameKey + ".delay", 5);
                                    for (String tagKey : lines.getKeys(false)) {
                                        if (!tagKey.equalsIgnoreCase("delay")) {
                                            tagList.add(config.getString("holograms." + key + ".animatedImage.frames." + frameKey + "." + tagKey));
                                        }
                                    }
                                    if (!tagList.isEmpty()) {
                                        frameList.add(new Frame(delay, tagList.toArray(new String[tagList.size()])));
                                    }
                                }
                            }
                        }
                        if (!frameList.isEmpty()) {
                            this.loadExtraData(new AnimatedHologramFactory(HoloAPI.getCore()).withText(new AnimatedTextGenerator(frameList.toArray(new Frame[frameList.size()]))).withSaveId(key).withLocation(new Vector(x, y, z), worldName).build(), key);
                        }
                    }
                } else {
                    ConfigurationSection cs1 = config.getConfigurationSection("holograms." + key + ".lines");
                    boolean containsImage = false;
                    if (cs1 != null) {
                        //ArrayList<String> lines = new ArrayList<String>();
                        HologramFactory hf = new HologramFactory(HoloAPI.getCore());
                        for (String key1 : cs1.getKeys(false)) {
                            if (StringUtil.isInt(key1)) {
                                String type = config.getString(path + "lines." + key1 + ".type");
                                String value = config.getString(path + "lines." + key1 + ".value");
                                if (type.equalsIgnoreCase("image")) {
                                    containsImage = true;
                                    break;
                                } else {
                                    hf.withText(value);
                                }

                            } else {
                                HoloAPICore.LOGGER.log(Level.WARNING, "Failed to load line section of " + key1 + " for Hologram of ID " + key + ".");
                            }
                        }
                        if (containsImage) {
                            unprepared.add(key);
                            continue;
                        }
                        this.loadExtraData(hf.withSaveId(key).withLocation(new Vector(x, y, z), worldName).build(), key);
                    }
                }

            }
        }
        return unprepared;
    }

    public Hologram loadFromFile(String hologramId) {
        String path = "holograms." + hologramId + ".";
        String worldName = config.getString(path + "worldName");
        double x = config.getDouble(path + "x");
        double y = config.getDouble(path + "y");
        double z = config.getDouble(path + "z");
        Hologram finalHologram = null;
        if (config.get(path + "animatedImage.image") != null) {
            if (config.getBoolean(path + "animatedImage.image")) {
                AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(config.getString(path + "animatedImage.key"));
                if (generator != null) {
                    finalHologram = new AnimatedHologramFactory(HoloAPI.getCore()).withImage(generator).withSaveId(hologramId).withLocation(new Vector(x, y, z), worldName).build();
                }
            }
        } else {
            ConfigurationSection cs1 = config.getConfigurationSection("holograms." + hologramId + ".lines");
            HologramFactory hf = new HologramFactory(HoloAPI.getCore());
            //ArrayList<String> lines = new ArrayList<String>();
            for (String key1 : cs1.getKeys(false)) {
                if (StringUtil.isInt(key1)) {
                    String type = config.getString(path + "lines." + key1 + ".type");
                    String value = config.getString(path + "lines." + key1 + ".value");
                    if (type.equalsIgnoreCase("image")) {
                        ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(value);
                        if (generator != null) {
                            hf.withImage(generator);
                        }
                    } else {
                        hf.withText(value);
                    }
                } else {
                    HoloAPICore.LOGGER.log(Level.WARNING, "Failed to load line section of " + key1 + " for Hologram of ID " + hologramId + ".");
                }
            }
            if (!hf.isEmpty()) {
                finalHologram = hf.withSaveId(hologramId).withLocation(new Vector(x, y, z), worldName).build();
            }
        }
        if (finalHologram != null) {
            this.loadExtraData(finalHologram, hologramId);
        }
        return finalHologram;
    }

    private void loadExtraData(Hologram hologram, String hologramKey) {
        String[] sections = new String[] {"touchactions", "visibility"};
        for (String sectionKey : sections) {
            ConfigurationSection section = this.config.getConfigurationSection("holograms." + hologramKey + "." + sectionKey);
            if (section != null) {
                for (String objKey : section.getKeys(true)) {
                    LinkedHashMap<String, Object> configMap = new LinkedHashMap<String, Object>();
                    ConfigurationSection objKeySection = this.config.getConfigurationSection("holograms." + hologramKey + "." + sectionKey + "." + objKey);
                    if (objKeySection != null) {
                        for (String fullKey : objKeySection.getKeys(true)) {
                            configMap.put(fullKey, objKeySection.get(fullKey));
                        }
                    }
                    this.callDataLoadEvent(sectionKey, hologram, objKey, configMap);
                }
            }
        }
    }

    private void callDataLoadEvent(String sectionkey, Hologram hologram, String objKey, LinkedHashMap<String, Object> configMap) {
        HoloDataLoadEvent event = new HoloDataLoadEvent(hologram, objKey, configMap);
        if (sectionkey.equalsIgnoreCase("touchactions")) {
            event = new HoloTouchActionLoadEvent(hologram, objKey, configMap);
        } else if (sectionkey.equalsIgnoreCase("visibility")) {
            event = new HoloVisibilityLoadEvent(hologram, objKey, configMap);
        }
        HoloAPI.getCore().getServer().getPluginManager().callEvent(event);
    }

    @Override
    public Hologram copy(Hologram hologram, Location copyLocation) {
        return hologram instanceof AnimatedHologram ? this.buildAnimatedCopy((AnimatedHologram) hologram, copyLocation).build() : this.buildCopy(hologram, copyLocation).build();
    }

    @Override
    public Hologram copyAndAddLineTo(Hologram original, String... linesToAdd) {
        if (original instanceof AnimatedHologram) {
            throw new IllegalArgumentException("Lines cannot be added to AnimatedHolograms.");
        }
        HologramFactory factory = this.buildCopy(original, original.getDefaultLocation()).withSaveId(original.getSaveId());
        for (String line : linesToAdd) {
            factory.withText(line);
        }
        return factory.build();
    }

    private AnimatedHologramFactory buildAnimatedCopy(AnimatedHologram original, Location copyLocation) {
        AnimatedHologramFactory animatedCopyFactory = new AnimatedHologramFactory(HoloAPI.getCore()).withLocation(copyLocation).withSimplicity(original.isSimple());
        if (original.isImageGenerated() && (HoloAPI.getAnimationLoader().exists(original.getAnimationKey())) || HoloAPI.getAnimationLoader().existsAsUnloadedUrl(original.getAnimationKey())) {
            animatedCopyFactory.withImage(HoloAPI.getAnimationLoader().getGenerator(original.getAnimationKey()));
        } else {
            ArrayList<Frame> frames = original.getFrames();
            animatedCopyFactory.withText(new AnimatedTextGenerator(frames.toArray(new Frame[frames.size()])));
        }
        return animatedCopyFactory;
    }

    private HologramFactory buildCopy(Hologram original, Location copyLocation) {
        HologramFactory copyFactory = new HologramFactory(HoloAPI.getCore()).withLocation(copyLocation).withSimplicity(original.isSimple());
        for (StoredTag tag : original.serialise()) {
            if (tag.isImage()) {
                ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(tag.getContent());
                if (generator != null) {
                    copyFactory.withImage(generator);
                }
            } else {
                copyFactory.withText(tag.getContent());
            }
        }
        return copyFactory;
    }

    @Override
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, List<String> lines) {
        return this.createSimpleHologram(location, secondsUntilRemoved, false, lines.toArray(new String[lines.size()]));
    }

    @Override
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, boolean rise, List<String> lines) {
        return this.createSimpleHologram(location, secondsUntilRemoved, rise, lines.toArray(new String[lines.size()]));
    }

    @Override
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, String... lines) {
        return this.createSimpleHologram(location, secondsUntilRemoved, false, lines);
    }

    @Override
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, boolean rise, String... lines) {
        int simpleId = TagIdGenerator.nextSimpleId(lines.length);
        final Hologram hologram = new HologramFactory(HoloAPI.getCore()).withFirstTagId(simpleId).withSaveId(simpleId + "").withText(lines).withLocation(location).withSimplicity(true).build();
        for (Entity e : hologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                hologram.show((Player) e, true);
            }
        }
        BukkitTask t = null;

        if (rise) {
            t = HoloAPI.getCore().getServer().getScheduler().runTaskTimer(HoloAPI.getCore(), new Runnable() {
                @Override
                public void run() {
                    Location l = hologram.getDefaultLocation();
                    l.add(0.0D, 0.02D, 0.0D);
                    hologram.move(l.toVector());
                }
            }, 1L, 1L);
        }

        new HologramRemoveTask(hologram, t).runTaskLater(HoloAPI.getCore(), secondsUntilRemoved * 20);
        return hologram;
    }

    @Override
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, Vector velocity, List<String> lines) {
        return this.createSimpleHologram(location, secondsUntilRemoved, velocity, lines.toArray(new String[lines.size()]));
    }

    @Override
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, final Vector velocity, String... lines) {
        int simpleId = TagIdGenerator.nextSimpleId(lines.length);
        final Hologram hologram = new HologramFactory(HoloAPI.getCore()).withFirstTagId(simpleId).withSaveId(simpleId + "").withText(lines).withLocation(location).withSimplicity(true).build();
        for (Entity e : hologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                hologram.show((Player) e, true);
            }
        }

        BukkitTask t = HoloAPI.getCore().getServer().getScheduler().runTaskTimer(HoloAPI.getCore(), new Runnable() {
            @Override
            public void run() {
                Location l = hologram.getDefaultLocation();
                l.add(velocity);
                hologram.move(l.toVector());
            }
        }, 1L, 1L);

        new HologramRemoveTask(hologram, t).runTaskLater(HoloAPI.getCore(), secondsUntilRemoved * 20);
        return hologram;
    }

    class HologramRemoveTask extends BukkitRunnable {

        BukkitTask t = null;
        private Hologram hologram;

        HologramRemoveTask(Hologram hologram, BukkitTask t) {
            this.hologram = hologram;
            this.t = t;
        }

        @Override
        public void run() {
            if (this.t != null) {
                t.cancel();
            }
            stopTracking(hologram);
        }
    }

    class UpdateDisplayTask extends BukkitRunnable {

        public UpdateDisplayTask() {
            this.runTaskTimer(HoloAPI.getCore(), 0L, 20 * 60);
        }

        @Override
        public void run() {
            if (!getAllHolograms().isEmpty()) {
                Iterator<Hologram> i = getAllHolograms().keySet().iterator();
                while (i.hasNext()) {
                    i.next().updateDisplay();
                }
            }
        }
    }
}
