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