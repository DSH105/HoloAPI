/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.holoapi.util.wrapper.protocol;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.reflection.FieldAccessor;
import com.dsh105.holoapi.reflection.SafeField;
import com.dsh105.holoapi.util.MiscUtil;
import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.PlayerUtil;
import com.dsh105.holoapi.util.ReflectionUtil;
import org.bukkit.entity.Player;

import java.util.Map;

public class Packet {

    private Class packetClass;
    private Object packetHandle;
    private Protocol protocol;
    private Sender sender;

    public Packet(PacketFactory.PacketType packetType) {
        this(packetType.getProtocol(), packetType.getSender(), packetType.getId(), packetType.getLegacyId());
    }

    public Packet(Protocol protocol, Sender sender, int id, int legacyId) {

        if (HoloAPI.isUsingNetty) {

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
            this.packetClass = (Class) MiscUtil.getKeyAtValue(map, legacyId);
            try {
                this.packetHandle = this.packetClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This class needs a lot improvement :/
     * @param packet
     */
    public Packet(Object packet) {
        this.packetHandle = packet;
        this.packetClass = packet.getClass();
    }

    public <T> T read(String fieldName) {
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
