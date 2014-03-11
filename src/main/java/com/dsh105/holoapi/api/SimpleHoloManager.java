package com.dsh105.holoapi.api;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.TagIdGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class SimpleHoloManager implements HoloManager {

    YAMLConfig config;
    private UpdateDisplayTask updateDisplayTask;
    private HashMap<Hologram, Plugin> holograms = new HashMap<Hologram, Plugin>();

    public SimpleHoloManager() {
        this.config = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.DATA);
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
            i.next().clearAllPlayerViews();
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
        if (this.updateDisplayTask == null) {
            this.updateDisplayTask = new UpdateDisplayTask();
        }
        if (!hologram.isSimple() && this.config.getConfigurationSection("holograms." + hologram.getSaveId()) == null) {
            this.saveToFile(hologram);
        }
        if (hologram instanceof AnimatedHologram && !((AnimatedHologram) hologram).isAnimating()) {
            ((AnimatedHologram) hologram).animate();
        }
    }

    @Override
    public void stopTracking(Hologram hologram) {
        hologram.clearAllPlayerViews();
        this.holograms.remove(hologram);
        if (this.holograms.isEmpty() && this.updateDisplayTask != null) {
            this.updateDisplayTask.cancel();
            this.updateDisplayTask = null;
        }
        if (hologram instanceof AnimatedHologram && ((AnimatedHologram) hologram).isAnimating()) {
            ((AnimatedHologram) hologram).cancelAnimation();
        }
        //this.clearFromFile(hologram);
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
                            this.config.set(path + "animatedImage.frames." + index + "." + tagIndex, tag);
                            tagIndex++;
                        }
                        index++;
                    }
                }
            } else {
                int index = 0;
                for (Map.Entry<String, Boolean> entry : hologram.serialise().entrySet()) {
                    this.config.set(path + "lines." + index + ".type", entry.getValue() ? "image" : "text");
                    this.config.set(path + "lines." + index + ".value", entry.getKey().replace(ChatColor.COLOR_CHAR, '&'));
                    index++;
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
                            new AnimatedHologramFactory(HoloAPI.getInstance()).withText(new AnimatedTextGenerator(frameList.toArray(new Frame[frameList.size()]))).withSaveId(key).withLocation(new Vector(x, y, z), worldName).build();
                        }
                    }
                } else {
                    ConfigurationSection cs1 = config.getConfigurationSection("holograms." + key + ".lines");
                    boolean containsImage = false;
                    if (cs1 != null) {
                        //ArrayList<String> lines = new ArrayList<String>();
                        HologramFactory hf = new HologramFactory(HoloAPI.getInstance());
                        for (String key1 : cs1.getKeys(false)) {
                            if (StringUtil.isInt(key1)) {
                                String type = config.getString(path + "lines." + key1 + ".type");
                                String value = config.getString(path + "lines." + key1 + ".value");
                                if (type.equalsIgnoreCase("image")) {
                                    containsImage = true;
                                    break;
                                } else {
                                    hf.withText(ChatColor.translateAlternateColorCodes('&', value));
                                }

                            } else {
                                HoloAPI.getInstance().LOGGER.log(Level.WARNING, "Failed to load line section of " + key1 + " for Hologram of ID " + key + ".");
                                continue;
                            }
                        }
                        if (containsImage) {
                            unprepared.add(key);
                            continue;
                        }
                        hf.withSaveId(key).withLocation(new Vector(x, y, z), worldName).build();
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
        if (config.get(path + "animatedImage.image") != null) {
            if (config.getBoolean(path + "animatedImage.image")) {
                AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(config.getString(path + "animatedImage.key"));
                if (generator != null) {
                    return new AnimatedHologramFactory(HoloAPI.getInstance()).withImage(generator).withSaveId(hologramId).withLocation(new Vector(x, y, z), worldName).build();
                }
            }
        } else {
            ConfigurationSection cs1 = config.getConfigurationSection("holograms." + hologramId + ".lines");
            HologramFactory hf = new HologramFactory(HoloAPI.getInstance());
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
                        hf.withText(ChatColor.translateAlternateColorCodes('&', value));
                    }
                } else {
                    HoloAPI.getInstance().LOGGER.log(Level.WARNING, "Failed to load line section of " + key1 + " for Hologram of ID " + hologramId + ".");
                    continue;
                }
            }
            if (!hf.isEmpty()) {
                return hf.withSaveId(hologramId).withLocation(new Vector(x, y, z), worldName).build();
            }
        }
        return null;
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
        final Hologram hologram = new HologramFactory(HoloAPI.getInstance()).withFirstTagId(simpleId).withSaveId(simpleId + "").withText(lines).withLocation(location).withSimplicity(true).build();
        for (Entity e : hologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                hologram.show((Player) e);
            }
        }
        BukkitTask t = null;

        if (rise) {
            final Location l = location.clone();
            t = HoloAPI.getInstance().getServer().getScheduler().runTaskTimer(HoloAPI.getInstance(), new Runnable() {
                @Override
                public void run() {
                    l.add(0.0D, 0.02D, 0.0D);
                    hologram.move(l.toVector());
                }
            }, 1L, 1L);
        }

        new HologramRemoveTask(hologram, t).runTaskLater(HoloAPI.getInstance(), secondsUntilRemoved * 20);
        return hologram;
    }

    @Override
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, Vector velocity, List<String> lines) {
        return this.createSimpleHologram(location, secondsUntilRemoved, velocity, lines);
    }

    @Override
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, final Vector velocity, String... lines) {
        int simpleId = TagIdGenerator.nextSimpleId(lines.length);
        final Hologram hologram = new HologramFactory(HoloAPI.getInstance()).withFirstTagId(simpleId).withSaveId(simpleId + "").withText(lines).withLocation(location).withSimplicity(true).build();
        for (Entity e : hologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                hologram.show((Player) e);
            }
        }

        final Location l = location.clone();
        BukkitTask t = HoloAPI.getInstance().getServer().getScheduler().runTaskTimer(HoloAPI.getInstance(), new Runnable() {
            @Override
            public void run() {
                l.add(velocity);
                hologram.move(l.toVector());
            }
        }, 1L, 1L);

        new HologramRemoveTask(hologram, t).runTaskLater(HoloAPI.getInstance(), secondsUntilRemoved * 20);
        return hologram;
    }

    class HologramRemoveTask extends BukkitRunnable {

        private Hologram hologram;
        BukkitTask t = null;

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
            for (Hologram h : getAllHolograms().keySet()) {
                if (h.isSimple()) {
                    h.refreshDisplay();
                }
            }
        }
    }

    class UpdateDisplayTask extends BukkitRunnable {

        public UpdateDisplayTask() {
            this.runTaskTimer(HoloAPI.getInstance(), 0L, 20 * 10);
        }

        @Override
        public void run() {
            Iterator<Hologram> i = getAllHolograms().keySet().iterator();
            while (i.hasNext()) {
                i.next().refreshDisplay();
            }
        }
    }
}
