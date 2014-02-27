package com.dsh105.holoapi.api;

import com.dsh105.dshutils.config.YAMLConfig;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public interface HoloManager {

    public HashMap<Hologram, Plugin> getAllHolograms();

    public ArrayList<Hologram> getHologramsFor(Plugin owningPlugin);

    public Hologram getHologram(String hologramId);

    public void track(Hologram hologram, Plugin owningPlugin);

    public void stopTracking(Hologram hologram);

    public void stopTracking(String hologramId);

    public void saveToFile(String hologramId);

    public void saveToFile(Hologram hologram);

    public void clearFromFile(String hologramId);

    public void clearFromFile(Hologram hologram);
}