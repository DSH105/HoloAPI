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
    public static final Field channelField = ReflectionUtil.getField(ReflectionUtil.getNMSClass("NetworkManager"), HoloAPI.getCore().SERVER.getMCVersion().contains("v1_7_R2") ? "m" : "k");
    public static final Field protocolPhase = ReflectionUtil.getField(ReflectionUtil.getNMSClass("NetworkManager"), HoloAPI.getCore().SERVER.getMCVersion().contains("v1_7_R2") ? "p" : "n");

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
