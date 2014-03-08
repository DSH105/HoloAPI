package com.dsh105.holoapi.util.wrapper.protocol;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.reflection.FieldAccessor;
import com.dsh105.holoapi.reflection.SafeField;
import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.PlayerUtil;
import com.dsh105.holoapi.util.ReflectionUtil;
import java.util.Map;
import org.bukkit.entity.Player;

public class Packet {

    private Class packetClass;
    private Object packetHandle;
    private Protocol protocol;
    private Sender sender;

    public Packet(PacketFactory.PacketType packetType) {
        this(packetType.getProtocol(), packetType.getSender(), packetType.getId(), packetType.getLegacyId());
    }

    public Packet(Protocol protocol, Sender sender, int id, int legacyId) {

        if(HoloAPI.isUsingNetty) {

            this.packetClass = PacketUtil.getPacket(protocol, sender, id);
            try {
                this.packetHandle = this.packetClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            FieldAccessor<Map> mapField = new SafeField<Map>(ReflectionUtil.getNMSClass("Packet"), "a");
            Map map = mapField.get(null);
            this.packetClass = (Class) map.get(legacyId);
            try {
                this.packetHandle = this.packetClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public Object read(String fieldName) {
        return ReflectionUtil.getField(getPacketClass(), fieldName, this.getPacketHandle());
    }

    public void write(String fieldName, Object value) {
        ReflectionUtil.setField(getPacketClass(), fieldName, getPacketHandle(), value);
    }

    public void send(Player receiver) {
        PlayerUtil.sendPacket(receiver, getPacketHandle());
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
