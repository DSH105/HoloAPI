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

import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;

public class WrapperPacketEntityTeleport extends Packet {

    public WrapperPacketEntityTeleport() {
        super(PacketFactory.PacketType.ENTITY_TELEPORT);
    }

    public void setEntityId(int value) {
        this.write("a", value);
    }

    public int getEntityId() {
        return (Integer) this.read("a");
    }

    public void setX(double value) {
        this.write("b", (int) Math.floor(value * 32.0D));
    }

    public double getX() {
        return (((Integer) this.read("b")) / 32.0D);
    }

    public void setY(double value) {
        this.write("c", (int) Math.floor(value * 32.0D));
    }

    public double getY() {
        return (((Integer) this.read("c")) / 32.0D);
    }

    public void setZ(double value) {
        this.write("d", (int) Math.floor(value * 32.0D));
    }

    public double getZ() {
        return (((Integer) this.read("d")) / 32.0D);
    }

    public void setYaw(float value) {
        this.write("e", (byte) (value * 256.0F / 360.0F));
    }

    public float getYaw() {
        return (((Byte) this.read("e")) * 360.0F / 256.0F);
    }

    public void setPitch(float value) {
        this.write("f", (byte) (value * 256.0F / 360.0F));
    }

    public float getPitch() {
        return (((Byte) this.read("f")) * 360.0F / 256.0F);
    }
}