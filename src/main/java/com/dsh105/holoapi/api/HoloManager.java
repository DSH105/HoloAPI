package com.dsh105.holoapi.api;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;

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