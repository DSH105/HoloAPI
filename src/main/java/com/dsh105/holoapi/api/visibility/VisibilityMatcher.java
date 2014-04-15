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

import java.util.HashMap;
import java.util.Map;

public class VisibilityMatcher {

    private HashMap<String, Visibility> visibilities = new HashMap<String, Visibility>();

    public VisibilityMatcher() {
        this.visibilities.put("all", new VisibilityAll());
        Visibility perm = new VisibilityPermission();
        this.visibilities.put("perm", perm);
        this.visibilities.put("permission", perm);
    }

    /**
     * Registers a visibility under a certain key
     *
     * @param key        key to register the visibility under
     * @param visibility visibility to register
     */
    public void add(String key, Visibility visibility) {
        this.visibilities.put(key, visibility);
    }

    /**
     * Removes a registration of a visibility
     *
     * @param key key of the registered visibility to remove
     */
    public void remove(String key) {
        this.visibilities.remove(key);
    }

    /**
     * Gets a visibility registered under a certain key
     *
     * @param key key to search for visibility with
     * @return Visibility that matches the specified key, null if it is not registered
     */
    public Visibility get(String key) {
        return this.visibilities.get(key);
    }

    /**
     * Gets the key of a registered visibility
     *
     * @param visibility visibility to search with
     * @return Key of the specified visibility, null if it is not registered
     */
    public String getKeyOf(Visibility visibility) {
        for (Map.Entry<String, Visibility> entry : this.visibilities.entrySet()) {
            if (visibility.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}