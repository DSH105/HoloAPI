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

package com.dsh105.holoapi.api.events;

import com.dsh105.holoapi.api.Hologram;

import java.util.Map;

/**
 * Called when TouchAction data is loaded from file. The event can be used to recreate TouchActions and add them to
 * loaded holograms. See {@link com.dsh105.holoapi.listeners.CommandTouchActionListener} for an example of this
 */
public class HoloTouchActionLoadEvent extends HoloEvent {

    private final String loadedTouchActionKey;
    private final Map<String, Object> configMap;

    public HoloTouchActionLoadEvent(Hologram hologram, String loadedTouchActionKey, Map<String, Object> configMap) {
        super(hologram);
        this.loadedTouchActionKey = loadedTouchActionKey;
        this.configMap = configMap;
    }

    /**
     * Gets the save key of the saved data
     *
     * @return key of the saved data
     */
    public String getLoadedTouchActionKey() {
        return loadedTouchActionKey;
    }

    /**
     * A map of all saved data for the appropriate key
     *
     * @return map of all saved data for the TouchAction
     */
    public Map<String, Object> getConfigMap() {
        return configMap;
    }

}