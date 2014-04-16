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

package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.reflection.utility.CommonReflection;
import com.dsh105.holoapi.util.ReflectionUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PlayerUtil {

    public static final Method getHandle = ReflectionUtil.getMethod(CommonReflection.getCraftEntityClass(), "getHandle");
    public static final Field playerConnection = ReflectionUtil.getField(CommonReflection.getMinecraftClass("EntityPlayer"), "playerConnection");
    public static final Method sendPacket = ReflectionUtil.getMethod(CommonReflection.getMinecraftClass("PlayerConnection"), "sendPacket", CommonReflection.getMinecraftClass("Packet"));
    public static final Field networkManager = ReflectionUtil.getField(CommonReflection.getMinecraftClass("PlayerConnection"), "networkManager");
    public static final Field channelField = ReflectionUtil.getField(CommonReflection.getMinecraftClass("NetworkManager"), CommonReflection.isUsingNetty() ? "m" : "k");
    public static final Field protocolPhase = ReflectionUtil.getField(CommonReflection.getMinecraftClass("NetworkManager"), CommonReflection.isUsingNetty() ? "p" : "n");

    public static Object toNMS(Player player) {
        return ReflectionUtil.invokeMethod(getHandle, player);
    }

    public static Object getPlayerConnection(Object nmsPlayer) {
        return ReflectionUtil.getField(playerConnection, nmsPlayer);
    }

    public static void sendPacket(Object playerConnection, Object packet) {
        ReflectionUtil.invokeMethod(sendPacket, playerConnection, packet);
    }

    public static Object getNetworkManager(Object playerConnection) {
        return ReflectionUtil.getField(networkManager, playerConnection);
    }

    public static Object getChannel(Object networkManager) {
        return ReflectionUtil.getField(channelField, networkManager);
    }

    public static Enum getProtocolPhase(Object networkManager) {
        return ReflectionUtil.getField(protocolPhase, networkManager);
    }
}
