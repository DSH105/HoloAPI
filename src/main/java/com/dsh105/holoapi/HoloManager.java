package com.dsh105.holoapi;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoloManager {

    YAMLConfig config;
    private HashMap<Hologram, Plugin> holograms = new HashMap<Hologram, Plugin>();

    public HoloManager() {
        this.config = HoloPlugin.getInstance().getConfig(HoloPlugin.ConfigType.DATA);
    }

    public HashMap<Hologram, Plugin> getAllHolograms() {
        return holograms;
    }

    public ArrayList<Hologram> getHologramsFor(Plugin owningPlugin) {
        ArrayList<Hologram> list = new ArrayList<Hologram>();
        for (Map.Entry<Hologram, Plugin> entry : this.holograms.entrySet()) {
            if (entry.getValue().equals(owningPlugin)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public Hologram getHologram(int id) {
        for (Hologram hologram : this.holograms.keySet()) {
            if (hologram.getId() == id) {
                return hologram;
            }
        }
        return null;
    }

    public void track(Hologram hologram, Plugin owningPlugin) {
        this.holograms.put(hologram, owningPlugin);
    }

    public void stopTracking(Hologram hologram) {
        this.holograms.remove(hologram);
    }

    public void stopTracking(int id) {
        Hologram hologram = this.getHologram(id);
        if (hologram != null) {
            this.stopTracking(hologram);
        }
    }

    public void saveToFile(int id) {
        Hologram hologram = this.getHologram(id);
        if (hologram != null) {
            this.saveToFile(hologram);
        }
    }

    public void saveToFile(Hologram hologram) {
        if (hologram.isPersistent() && this.isValid(hologram.getSaveId())) {
            this.config.set(hologram.getSaveId() + ".x", hologram.getDefaultX());
            this.config.set(hologram.getSaveId() + ".y", hologram.getDefaultY());
            this.config.set(hologram.getSaveId() + ".z", hologram.getDefaultZ());
            this.config.set(hologram.getSaveId() + ".lines", hologram.getLines());
        }
    }

    public Hologram createFromFile(String saveId) {
        ConfigurationSection cs = this.config.getConfigurationSection(saveId);
        if (cs != null) {
            double[] coords = new double[] {
                    this.config.getDouble(saveId + ".x"),
                    this.config.getDouble(saveId + ".y"),
                    this.config.getDouble(saveId + ".z")
            };
            List<String> lines = Arrays.asList(this.config.getList(saveId + ".lines").toString());
            //List<String> lines = (ArrayList<String>) this.config.getList(saveId + ".lines");
            if (lines == null) {
                return null;
            }

            Hologram hologram = new HologramFactory()
                    .withCoords(coords[0], coords[1], coords[2])
                    .withText(lines.toArray(new String[lines.size()]))
                    .build();
            hologram.setSaveId(saveId);
            this.track(hologram, HoloPlugin.getInstance());
            return hologram;
        }
        return null;
    }

    private boolean isValid(String saveId) {
        return saveId != null && !"".equals(saveId) && saveId.length() > 4;
    }
}