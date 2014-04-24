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

package com.dsh105.holoapi.util.wrapper;

import com.dsh105.holoapi.reflection.Constants;
import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;

public class WrapperPacketAttachEntity extends Packet {

    public WrapperPacketAttachEntity() {
        super(PacketFactory.PacketType.ENTITY_ATTACH);
    }

    public boolean getLeashed() {
        return (Integer) this.read(Constants.PACKET_ATTACHENTITY_FIELD_LEASHED.getName()) != 0;
    }

    public void setLeashed(boolean flag) {
        this.write(Constants.PACKET_ATTACHENTITY_FIELD_LEASHED.getName(), flag ? 1 : 0);
    }

    public int getEntityId() {
        return (Integer) this.read(Constants.PACKET_ATTACHENTITY_FIELD_ENTITYID.getName());
    }

    public void setEntityId(int value) {
        this.write(Constants.PACKET_ATTACHENTITY_FIELD_ENTITYID.getName(), value);
    }

    public int getVehicleId() {
        return (Integer) this.read(Constants.PACKET_ATTACHENTITY_FIELD_VEHICLEID.getName());
    }

    public void setVehicleId(int value) {
        this.write(Constants.PACKET_ATTACHENTITY_FIELD_VEHICLEID.getName(), value);
    }
}