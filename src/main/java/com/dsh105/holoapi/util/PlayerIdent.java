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

package com.dsh105.holoapi.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerIdent {

    public static Object getIdentificationFor(Player player) {
        if (ReflectionUtil.MC_VERSION_NUMERIC >= 172) {
            return player.getUniqueId();
        } else {
            return player.getName();
        }
    }

    public static String getIdentificationForAsString(Player player) {
        if (ReflectionUtil.MC_VERSION_NUMERIC >= 172) {
            return player.getUniqueId().toString();
        } else {
            return player.getName();
        }
    }

    public static Player getPlayerOf(String identification) {
        if (ReflectionUtil.MC_VERSION_NUMERIC >= 172) {
            return Bukkit.getPlayer(UUID.fromString(identification));
        } else {
            return Bukkit.getPlayerExact(identification);
        }
    }
}