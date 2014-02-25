package com.dsh105.holoapi.api;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.holoapi.HoloAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class SimpleHoloManager implements HoloManager {

    YAMLConfig config;
    private HashMap<Hologram, Plugin> holograms = new HashMap<Hologram, Plugin>();

    public SimpleHoloManager() {
        this.config = HoloAPI.getInstance().getConfig(HoloAPI.ConfigType.DATA);
    }

    public HashMap<Hologram, Plugin> getAllHolograms() {
        return holograms;
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
    }

    @Override
    public void stopTracking(Hologram hologram) {
        this.holograms.remove(hologram);
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
        this.config.set(hologram.getFirstId() + ".world", hologram.getWorldName());
        this.config.set(hologram.getFirstId() + ".x", hologram.getDefaultX());
        this.config.set(hologram.getFirstId() + ".y", hologram.getDefaultY());
        this.config.set(hologram.getFirstId() + ".z", hologram.getDefaultZ());
        this.config.set(hologram.getFirstId() + ".lines", hologram.getLines());
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
            this.track(hologram, HoloAPI.getInstance());
            return hologram;
        }
        return null;
    }
}