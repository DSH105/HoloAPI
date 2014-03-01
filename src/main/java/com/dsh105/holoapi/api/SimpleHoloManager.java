package com.dsh105.holoapi.api;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.dshutils.util.GeometryUtil;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.image.ImageGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

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
        if (this.config.getConfigurationSection("holograms." + hologram.getSaveId()) == null) {
            this.saveToFile(hologram);
        }
        if (hologram instanceof AnimatedHologram && ((AnimatedHologram) hologram).isAnimating()) {
            ((AnimatedHologram) hologram).restartAnimation();
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
        this.config.set("holograms." + hologram.getSaveId() + ".worldName", hologram.getWorldName());
        this.config.set("holograms." + hologram.getSaveId() + ".x", hologram.getDefaultX());
        this.config.set("holograms." + hologram.getSaveId() + ".y", hologram.getDefaultY());
        this.config.set("holograms." + hologram.getSaveId() + ".z", hologram.getDefaultZ());
        int index = 0;
        for (Map.Entry<String, Boolean> entry : hologram.serialise().entrySet()) {
            this.config.set("holograms." + hologram.getSaveId() + ".lines." + index + ".type", entry.getValue() ? "image" : "text");
            this.config.set("holograms." + hologram.getSaveId() + ".lines." + index + ".value", entry.getKey().replace(ChatColor.COLOR_CHAR, '&'));
            index++;
        }
        this.config.saveConfig();
    }

    @Override
    public void clearFromFile(String hologramId) {
        Hologram hologram = this.getHologram(hologramId);
        if (hologram != null) {
            this.clearFromFile(hologram);
        }
    }

    @Override
    public void clearFromFile(Hologram hologram) {
        this.config.set("holograms." + hologram.getSaveId() + "", null);
        this.config.saveConfig();
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
                ConfigurationSection cs1 = config.getConfigurationSection("holograms." + key + ".lines");
                boolean containsImage = false;
                if (cs1 != null) {
                    //ArrayList<String> lines = new ArrayList<String>();
                    HologramFactory hf = new HologramFactory();
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
        return unprepared;
    }

    public Hologram loadFromFile(String hologramId) {
        String path = "holograms." + hologramId + ".";
        String worldName = config.getString(path + "worldName");
        double x = config.getDouble(path + "x");
        double y = config.getDouble(path + "y");
        double z = config.getDouble(path + "z");
        ConfigurationSection cs1 = config.getConfigurationSection("holograms." + hologramId + ".lines");
        HologramFactory hf = new HologramFactory();
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
        return null;
    }

    class UpdateDisplayTask extends BukkitRunnable {

        public UpdateDisplayTask() {
            this.runTaskTimer(HoloAPI.getInstance(), 0L, 20 * 30);
        }

        private ArrayList<Hologram> toUpdate = new ArrayList<Hologram>();

        @Override
        public void run() {
            Iterator<Hologram> i = toUpdate.iterator();
            while (i.hasNext()) {
                Hologram h = i.next();
                HashMap<String, Vector> map = new HashMap<String, Vector>();
                map.putAll(h.playerToLocationMap);

                for (String name : map.keySet()) {
                    Player p = Bukkit.getPlayerExact(name);
                    if (!GeometryUtil.getNearbyEntities(h.getDefaultLocation(), 50).contains(p)) {
                        h.clear(p);
                    } else {
                        h.show(p);
                    }
                }
            }
        }
    }
}