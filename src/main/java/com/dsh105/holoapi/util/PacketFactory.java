package com.dsh105.holoapi.util;

import com.dsh105.holoapi.util.wrapper.protocol.*;

public class PacketFactory {

    public enum PacketType {
        ENTITY_SPAWN(Protocol.PLAY, Sender.SERVER, 14),
        ENTITY_LIVING_SPAWN(Protocol.PLAY, Sender.SERVER, 15),
        ENTITY_ATTACH(Protocol.PLAY, Sender.SERVER, 27);

        private Protocol protocol;
        private Sender sender;
        private int id;

        PacketType(Protocol protocol, Sender sender, int id) {
            this.protocol = protocol;
            this.sender = sender;
            this.id = id;
        }

        public Protocol getProtocol() {
            return protocol;
        }

        public Sender getSender() {
            return sender;
        }

        public int getId() {
            return id;
        }
    }

    //TODO

}
