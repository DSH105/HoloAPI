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

import java.util.*;
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
            i.next().clearPlayerLocationMap();
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
    public Hologram getHologram(int id) {
        for (Hologram hologram : this.holograms.keySet()) {
            if (hologram.getFirstId() == id) {
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
        this.saveToFile(hologram);
    }

    @Override
    public void stopTracking(Hologram hologram) {
        hologram.clearPlayerLocationMap();
        this.holograms.remove(hologram);
        if (this.holograms.isEmpty() && this.updateDisplayTask != null) {
            this.updateDisplayTask.cancel();
            this.updateDisplayTask = null;
        }
        this.clearFromFile(hologram);
    }

    @Override
    public void stopTracking(int id) {
        Hologram hologram = this.getHologram(id);
        if (hologram != null) {
            this.stopTracking(hologram);
        }
    }

    @Override
    public void saveToFile(int id) {
        Hologram hologram = this.getHologram(id);
        if (hologram != null) {
            this.saveToFile(hologram);
        }
    }

    @Override
    public void saveToFile(Hologram hologram) {
        this.config.set("holograms." + hologram.getFirstId() + ".worldName", hologram.getWorldName());
        this.config.set("holograms." + hologram.getFirstId() + ".x", hologram.getDefaultX());
        this.config.set("holograms." + hologram.getFirstId() + ".y", hologram.getDefaultY());
        this.config.set("holograms." + hologram.getFirstId() + ".z", hologram.getDefaultZ());
        int index = 0;
        for (Map.Entry<String, Boolean> entry : hologram.serialise().entrySet()) {
            this.config.set("holograms." + hologram.getFirstId() + ".lines." + index + ".type", entry.getValue() ? "image" : "text");
            this.config.set("holograms." + hologram.getFirstId() + ".lines." + index + ".value", entry.getKey().replace(ChatColor.COLOR_CHAR, '&'));
            index++;
        }
        this.config.saveConfig();
    }

    @Override
    public void clearFromFile(int id) {
        Hologram hologram = this.getHologram(id);
        if (hologram != null) {
            this.clearFromFile(hologram);
        }
    }

    @Override
    public void clearFromFile(Hologram hologram) {
        this.config.set("holograms." + hologram.getFirstId() + "", null);
        this.config.saveConfig();
    }

    public ArrayList<String> loadFileData() {
        ArrayList<String> unprepared = new ArrayList<String>();
        ConfigurationSection cs = config.getConfigurationSection("holograms");
        if (cs != null) {
            for (String key : cs.getKeys(false)) {
                String path = "holograms." + key + ".";
                String worldName = config.getString(path + "worldName");
                int x = config.getInt(path + "x");
                int y = config.getInt(path + "y");
                int z = config.getInt(path + "z");
                ConfigurationSection cs1 = config.getConfigurationSection("holograms." + key + ".lines");
                boolean containsImage = false;
                if (cs1 != null) {
                    ArrayList<String> lines = new ArrayList<String>();
                    for (String key1 : cs1.getKeys(false)) {
                        if (StringUtil.isInt(key1)) {
                            String type = config.getString(path + "lines." + key1 + ".type");
                            String value = config.getString(path + "lines." + key1 + ".value");
                            if (type.equalsIgnoreCase("image")) {
                                containsImage = true;
                                break;
                            /*ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(value);
                            if (generator != null) {
                                for (String line : generator.getLines()) {
                                    lines.add(line);
                                }
                            }*/
                            } else {
                                lines.add(Integer.parseInt(key1), ChatColor.translateAlternateColorCodes('&', value));
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
                    if (!lines.isEmpty()) {
                        new HologramFactory().withFirstId(Integer.parseInt(key)).withText(lines.toArray(new String[lines.size()])).withLocation(new Vector(x, y, z), worldName).build();
                    }
                }
            }
        }
        return unprepared;
    }

    public void loadFromFile(int hologramId) {
        String path = "holograms." + hologramId + ".";
        String worldName = config.getString(path + "worldName");
        int x = config.getInt(path + "x");
        int y = config.getInt(path + "y");
        int z = config.getInt(path + "z");
        ConfigurationSection cs1 = config.getConfigurationSection("holograms." + hologramId + ".lines");
        ArrayList<String> lines = new ArrayList<String>();
        for (String key1 : cs1.getKeys(false)) {
            if (StringUtil.isInt(key1)) {
                String type = config.getString(path + "lines." + key1 + ".type");
                String value = config.getString(path + "lines." + key1 + ".value");
                if (type.equalsIgnoreCase("image")) {
                    ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(value);
                    if (generator != null) {
                        for (String line : generator.getLines()) {
                            lines.add(line);
                        }
                    }
                } else {
                    lines.add(ChatColor.translateAlternateColorCodes('&', value));
                }
            } else {
                HoloAPI.getInstance().LOGGER.log(Level.WARNING, "Failed to load line section of " + key1 + " for Hologram of ID " + hologramId + ".");
                continue;
            }
        }
        if (!lines.isEmpty()) {
            new HologramFactory().withFirstId(hologramId).withText(lines.toArray(new String[lines.size()])).withLocation(new Vector(x, y, z), worldName).build();
        }
    }

    @Override
    public Hologram createFromFile(int saveId) {
        ConfigurationSection cs = this.config.getConfigurationSection("" + saveId);
        if (cs != null) {
            double[] coords = new double[] {
                    this.config.getDouble(saveId + ".x"),
                    this.config.getDouble(saveId + ".y"),
                    this.config.getDouble(saveId + ".z")
            };
            List<String> lines = Arrays.asList(this.config.getList(saveId + ".lines").toString());
            if (lines == null) {
                return null;
            }

            Hologram hologram = new HologramFactory()
                    .withLocation(new org.bukkit.util.Vector(coords[0], coords[1], coords[2]), this.config.getString(saveId + ".world"))
                    .withText(lines.toArray(new String[lines.size()]))
                    .withFirstId(saveId)
                    .build();
            return hologram;
        }
        return null;
    }

    class UpdateDisplayTask extends BukkitRunnable {

        public UpdateDisplayTask() {
            this.runTaskTimer(HoloAPI.getInstance(), 0L, 20*30);
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
                    }
                }
            }
        }
    }
}