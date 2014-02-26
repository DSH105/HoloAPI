package com.dsh105.holoapi.util;

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class PlayerUtil {

    private static final Method sendPacket = ReflectionUtil.getMethod(ReflectionUtil.getNMSClass("PlayerConnection"), "sendPacket", ReflectionUtil.getNMSClass("Packet"));

    public static void sendPacket(Player player, Object packet){
        Object playerConnection = getPlayerConnection(player);
        try {
            sendPacket.invoke(playerConnection, packet);
        } catch (Exception e) {
            HoloAPI.LOGGER_REFLECTION.warning("Failed to retrieve the PlayerConnection of: " + player.getName());
        }
    }

    public static Object playerToEntityPlayer(Player player){
        Method getHandle = ReflectionUtil.getMethod(player.getClass(), "getHandle");
        try {
            return getHandle.invoke(player);
        } catch (Exception e) {
            HoloAPI.LOGGER_REFLECTION.warning("Failed retrieve the NMS Player-Object of:" + player.getName());
            return null;
        }
    }

    public static Object getPlayerConnection(Player player){
        Object connection = ReflectionUtil.getField(ReflectionUtil.getNMSClass("EntityPlayer"), "playerConnection", playerToEntityPlayer(player));
        return connection;
    }
}
