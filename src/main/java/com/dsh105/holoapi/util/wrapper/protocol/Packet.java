package com.dsh105.holoapi.util.wrapper.protocol;

import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.ReflectionUtil;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Packet {

    private Class packetClass;
    private net.minecraft.server.v1_7_R1.Packet packetHandle;
    private Protocol protocol;
    private Sender sender;

    public Packet(PacketFactory.PacketType packetType) {
        this(packetType.getProtocol(), packetType.getSender(), packetType.getId());
    }

    public Packet(Protocol protocol, Sender sender, int id) {
        this.packetClass = PacketUtil.getPacket(protocol, sender, id);
        try {
            this.packetHandle = (net.minecraft.server.v1_7_R1.Packet) this.packetClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object read(String fieldName) {
        return ReflectionUtil.getField(getPacketClass(), fieldName, this.getPacketHandle());
    }

    public void write(String fieldName, Object value) {
        ReflectionUtil.setField(getPacketClass(), fieldName, getPacketHandle(), value);
    }

    public void send(Player receiver) {
        ((CraftPlayer) receiver).getHandle().playerConnection.sendPacket(this.getPacketHandle());
    }

    public Class getPacketClass() {
        return this.packetClass;
    }

    public net.minecraft.server.v1_7_R1.Packet getPacketHandle() {
        return this.packetHandle;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public Sender getSender() {
        return this.sender;
    }
}
