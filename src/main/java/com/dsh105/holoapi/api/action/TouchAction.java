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

package com.dsh105.holoapi.api.action;

import com.avaje.ebean.validation.NotNull;
import com.dsh105.holoapi.protocol.Action;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

/**
 * Represents an action that is performed when a Hologram is touched
 */
public interface TouchAction {

    /**
     * Action to perform when a Hologram that the action is attached to is touched
     *
     * @param who Player interacting with the Hologram
     */
    public void onTouch(Player who, Action action);

    /**
     * Gets the String the represents the TouchAction. The serialised key is used in hologram information lists within HoloAPI
     *
     * @return string that represents the TouchAction
     */
    public @NotNull String getSaveKey();

    /**
     * Gets a map of the TouchAction data to save to file.
     * <p>
     * HoloAPI uses this data for saving Hologram TouchAction data to file so that it can be loaded again when the hologram is recreated from the save file. See {@link com.dsh105.holoapi.api.event.TouchActionLoadEvent} for information on how to load TouchAction data back into holograms
     *
     * @return a map of all data to save to file. See {@link com.dsh105.holoapi.api.action.CommandTouchAction} for a working example of this
     */
    public LinkedHashMap<String, Object> getDataToSave();
}