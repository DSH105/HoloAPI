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

    public ArrayList<Hologram> getHologramsFor(Plugin owningPlugin);

    public Hologram getHologram(int id);

    public void track(Hologram hologram, Plugin owningPlugin);

    public void stopTracking(Hologram hologram);

    public void stopTracking(int id);

    public void saveToFile(int id);

    public void saveToFile(Hologram hologram);

    public Hologram createFromFile(int saveId);
}