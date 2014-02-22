package com.dsh105.holoapi.util.wrapper.protocol;

import com.dsh105.holoapi.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

public class PacketUtil {

    public static final Class CLASS_TEMPLATE = ReflectionUtil.getNMSClass("EnumProtocol");
    private static final Field SERVER_PACKET_MAP= ReflectionUtil.getField(CLASS_TEMPLATE, "i");
    private static final Field CLIENT_PACKET_MAP = ReflectionUtil.getField(CLASS_TEMPLATE, "h");

    public static Class getPacket(Protocol protocol, Sender sender, int id) {
        if(sender == Sender.CLIENT) {
            try {
                return (Class) ((Map) CLIENT_PACKET_MAP.get(protocol.toVanilla())).get(id);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if(sender == Sender.SERVER) {
            try {
                return (Class) ((Map) SERVER_PACKET_MAP.get(protocol.toVanilla())).get(id);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
