package com.dsh105.holoapi.util.wrapper.protocol;

import com.dsh105.holoapi.util.ReflectionUtil;

public class Packet {

    private Class packetClass;
    private Object packetHandle;
    private Protocol protocol;
    private Sender sender;

    public Packet(Protocol protocol, Sender sender, int id) {
        this.packetClass = PacketUtil.getPacket(protocol, sender, id);
        try {
            this.packetHandle = this.packetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void write(String fieldName, Object value) {
        ReflectionUtil.setField(getPacketClass(), fieldName, getPacketHandle(), value);
    }

    public Class getPacketClass() {
        return this.packetClass;
    }

    public Object getPacketHandle() {
        return this.packetHandle;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public Sender getSender() {
        return this.sender;
    }
}
