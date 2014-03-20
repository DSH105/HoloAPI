package com.dsh105.holoapi.listeners;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.SimpleHoloManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class WorldListener implements Listener {

    private static HashMap<String, String> UNLOADED_HOLOGRAMS = new HashMap<String, String>();

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        for (Map.Entry<String, String> entry : UNLOADED_HOLOGRAMS.entrySet()) {
            if (entry.getValue().equals(event.getWorld().getName())) {
                HoloAPI.LOGGER.log(Level.INFO, "Attempting to load hologram " + entry.getKey() + " into world " + entry.getValue() + ".");
                if (((SimpleHoloManager) HoloAPI.getManager()).loadFromFile(entry.getKey()) != null) {
                    UNLOADED_HOLOGRAMS.remove(entry.getKey());
                }
            }
        }
    }

    public static void store(String hologramId, String worldName) {
        UNLOADED_HOLOGRAMS.put(hologramId, worldName);
    }
}