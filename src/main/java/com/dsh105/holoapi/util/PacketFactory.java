package com.dsh105.holoapi.util;

import com.dsh105.holoapi.util.wrapper.protocol.*;

public class PacketFactory {

    public static final Packet ENTITY_SPAWN = new Packet(Protocol.PLAY, Sender.SERVER, 14);
    public static final Packet ENTITY_ATTACH = new Packet(Protocol.PLAY, Sender.SERVER, 27);

}
