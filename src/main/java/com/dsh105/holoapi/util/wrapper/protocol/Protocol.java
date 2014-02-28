package com.dsh105.holoapi.util.wrapper.protocol;

import com.dsh105.holoapi.util.ReflectionUtil;

public enum Protocol {

    HANDSHAKE,
    PLAY,
    STATUS,
    LOGIN;

    public static Protocol fromVanilla(Enum<?> enumValue) {
        String name = enumValue.name();

        if ("HANDSHAKING".equals(name))
            return HANDSHAKE;
        if ("PLAY".equals(name))
            return PLAY;
        if ("STATUS".equals(name))
            return STATUS;
        if ("LOGIN".equals(name))
            return LOGIN;

        return null;
    }

    public Object toVanilla() {
        switch (this) {
            case HANDSHAKE:
                return Enum.valueOf(ReflectionUtil.getNMSClass("EnumProtocol"), "HANDSHAKING");
            case PLAY:
                return Enum.valueOf(ReflectionUtil.getNMSClass("EnumProtocol"), "PLAY");
            case STATUS:
                return Enum.valueOf(ReflectionUtil.getNMSClass("EnumProtocol"), "STATUS");
            case LOGIN:
                return Enum.valueOf(ReflectionUtil.getNMSClass("EnumProtocol"), "LOGIN");
            default:
                return null;
        }
    }
}
