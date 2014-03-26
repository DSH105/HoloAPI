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

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PlayerUtil {

    private static final Method sendPacket = ReflectionUtil.getMethod(ReflectionUtil.getNMSClass("PlayerConnection"), "sendPacket", ReflectionUtil.getNMSClass("Packet"));

    public static void sendPacket(Player player, Object packet) {
        Object playerConnection = getPlayerConnection(player);
        try {
            sendPacket.invoke(playerConnection, packet);
        } catch (IllegalAccessException e) {
            HoloAPI.LOGGER_REFLECTION.warning("Failed to retrieve the PlayerConnection of: " + player.getName());
        } catch (IllegalArgumentException e) {
            HoloAPI.LOGGER_REFLECTION.warning("Failed to retrieve the PlayerConnection of: " + player.getName());
        } catch (InvocationTargetException e) {
            HoloAPI.LOGGER_REFLECTION.warning("Failed to retrieve the PlayerConnection of: " + player.getName());
        }
    }

    public static Object playerToEntityPlayer(Player player) {
        Method getHandle = ReflectionUtil.getMethod(player.getClass(), "getHandle");
        try {
            return getHandle.invoke(player);
        } catch (IllegalAccessException e) {
            HoloAPI.LOGGER_REFLECTION.warning("Failed retrieve the NMS Player-Object of:" + player.getName());
            return null;
        } catch (IllegalArgumentException e) {
            HoloAPI.LOGGER_REFLECTION.warning("Failed retrieve the NMS Player-Object of:" + player.getName());
            return null;
        } catch (InvocationTargetException e) {
            HoloAPI.LOGGER_REFLECTION.warning("Failed retrieve the NMS Player-Object of:" + player.getName());
            return null;
        }
    }

    public static Object getPlayerConnection(Player player) {
        return ReflectionUtil.getField(ReflectionUtil.getNMSClass("EntityPlayer"), "playerConnection", playerToEntityPlayer(player));
    }
}
