package com.dsh105.holoapi.protocol;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.ReflectionUtil;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PlayerUtil {

    public static final Method getHandle = ReflectionUtil.getMethod(ReflectionUtil.getCBCClass("entity.CraftEntity"), "getHandle");
    public static final Field playerConnection = ReflectionUtil.getField(ReflectionUtil.getNMSClass("EntityPlayer"), "playerConnection");
    public static final Method sendPacket = ReflectionUtil.getMethod(ReflectionUtil.getNMSClass("PlayerConnection"), "sendPacket", ReflectionUtil.getNMSClass("Packet"));
    public static final Field networkManager = ReflectionUtil.getField(ReflectionUtil.getNMSClass("PlayerConnection"), "networkManager");
    public static final Field channelField = ReflectionUtil.getField(ReflectionUtil.getNMSClass("NetworkManager"), HoloAPI.isUsingNetty ? "m" : "k");
    public static final Field protocolPhase = ReflectionUtil.getField(ReflectionUtil.getNMSClass("NetworkManager"), HoloAPI.isUsingNetty ? "p" : "n");

    public static Object toNMS(Player player) {
        return ReflectionUtil.invokeMethod(getHandle, player);
    }

    public static Object getPlayerConnection(Player player) {
        return ReflectionUtil.getField(playerConnection, toNMS(player));
    }

    public static void sendPacket(Player player, Object packet) {
        ReflectionUtil.invokeMethod(sendPacket, getPlayerConnection(player), packet);
    }

    public static Object getNetworkManager(Player player) {
        return ReflectionUtil.getField(networkManager, getPlayerConnection(player));
    }

    public static Object getChannel(Player player) {
        return ReflectionUtil.getField(channelField, getNetworkManager(player));
    }

    public static Enum getProtocolPhase(Player player) {
        return ReflectionUtil.getField(protocolPhase, getNetworkManager(player));
    }
}
