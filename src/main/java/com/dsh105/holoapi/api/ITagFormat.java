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

package com.dsh105.holoapi.api;

import org.bukkit.entity.Player;

public interface ITagFormat {

    /**
     * Gets the value to use in the hologram instead of the registered key
     *
     * @param observer player viewing the hologram
     * @deprecated use {@link com.dsh105.holoapi.api.ITagFormat#getValue(Hologram, org.bukkit.entity.Player)}
     * @return
     */
    @Deprecated
    public String getValue(Player observer);

    /**
     * Gets the value to use in the hologram instead of the registered key
     *
     * @param hologram hologram using this function
     * @param observer player viewing the hologram
     * @return
     */
    public String getValue(Hologram hologram, Player observer);
}