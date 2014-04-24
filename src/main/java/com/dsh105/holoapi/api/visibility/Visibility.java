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

package com.dsh105.holoapi.api.visibility;

import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

/**
 * Represents the visibility of the hologram
 */

public interface Visibility {

    /**
     * Gets whether a hologram can be shown to a certain player
     *
     * @param player player in question
     * @return true if hologram can be shown to the player, false if it should be kept hidden
     */
    public boolean isVisibleTo(Player player, String hologramId);

    /**
     * Gets the String the represents the Visibility. The save key is used in hologram information lists and data files within HoloAPI
     *
     * @return string that represents the Visibility
     */
    public String getSaveKey();

    /**
     * Gets a map of the Visibility data to save to file.
     * <p/>
     * HoloAPI uses this data for saving Hologram Visibility data to file so that it can be loaded again when the hologram is recreated from the save file. See {@link com.dsh105.holoapi.api.events.HoloVisibilityLoadEvent} for information on how to load TouchAction data back into holograms
     *
     * @return a map of all data to save to file. See {@link com.dsh105.holoapi.api.touch.CommandTouchAction} for a working example of this
     */
    public LinkedHashMap<String, Object> getDataToSave();
}