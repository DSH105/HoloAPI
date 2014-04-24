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
 * Called when data is loaded from file. The event can be used to recreate TouchActions or a certain Visibility and add them to
 * loaded holograms. See {@link com.dsh105.holoapi.listeners.HoloDataLoadListener} for an example of this
 */
public class HoloDataLoadEvent extends HoloEvent {

    private final String saveKey;
    private final Map<String, Object> configMap;

    public HoloDataLoadEvent(Hologram hologram, String saveKey, Map<String, Object> configMap) {
        super(hologram);
        this.saveKey = saveKey;
        this.configMap = configMap;
    }

    /**
     * Gets the save key of the saved data
     *
     * @return key of the saved data
     */
    public String getSaveKey() {
        return saveKey;
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