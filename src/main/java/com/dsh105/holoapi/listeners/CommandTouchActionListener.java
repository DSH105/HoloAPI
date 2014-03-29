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
import com.dsh105.holoapi.api.touch.CommandTouchAction;
import com.dsh105.holoapi.api.events.HoloTouchActionLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class CommandTouchActionListener implements Listener {

    @EventHandler
    public void onTouchActionLoad(HoloTouchActionLoadEvent event) {
        // Make sure it's what we're looking for
        if (event.getLoadedTouchActionKey().startsWith("command_")) {
            // Just in-case (for some reason) the command data didn't actually save
            if (event.getConfigMap().get("command") != null) {
                try {
                    Object asConsole = event.getConfigMap().get("asConsole");
                    event.getHologram().addTouchAction(new CommandTouchAction((String) event.getConfigMap().get("command"), (asConsole != null && asConsole instanceof Boolean) ? (Boolean) asConsole : false));
                } catch (ClassCastException e) {
                    HoloAPI.LOGGER.log(Level.SEVERE, "Failed to load command touch action data for hologram (" + event.getHologram().getSaveId() + "). Maybe the save data was edited?");
                }
            }
        }
    }
}