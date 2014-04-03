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

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;
import org.bukkit.util.Vector;

public class WrapperPacketSpawnEntityLiving extends Packet {

    public WrapperPacketSpawnEntityLiving() {
        super(PacketFactory.PacketType.ENTITY_LIVING_SPAWN);
    }

    public void setEntityId(int value) {
        this.write("a", value);
    }

    public int getEntityId() {
        return (Integer) this.read("a");
    }

    public void setEntityType(int value) {
        this.write("b", value);
    }

    public int getEntityType() {
        return (Integer) this.read("b");
    }

    public void setX(double value) {
        this.write("c", (int) Math.floor(value * 32.0D));
    }

    public double getX() {
        return (((Integer) this.read("c")) / 32.0D);
    }

    public void setY(double value) {
        this.write("d", (int) Math.floor(value * 32.0D));
    }

    public double getY() {
        return (((Integer) this.read("d")) / 32.0D);
    }

    public void setZ(double value) {
        this.write("e", (int) Math.floor(value * 32.0D));
    }

    public double getZ() {
        return (((Integer) this.read("e")) / 32.0D);
    }

    public void setYaw(float value) {
        this.write("i", (byte) (value * 256.0F / 360.0F));
    }

    public float getYaw() {
        return (((Byte) this.read("i")) * 360.0F / 256.0F);
    }

    public void setHeadPitch(float value) {
        this.write("j", (byte) (value * 256.0F / 360.0F));
    }

    public float getHeadPitch() {
        return (((Byte) this.read("j")) * 360.0F / 256.0F);
    }

    public void setHeadYaw(float value) {
        this.write("k", (byte) (value * 256.0F / 360.0F));
    }

    public float getHeadYaw() {
        return (((Byte) this.read("k")) * 360.0F / 256.0F);
    }

    public Vector getVelocity() {
        return new Vector(this.getMotionX(), this.getMotionY(), this.getMotionZ());
    }

    public void setVelocity(Vector v) {
        this.setMotionX(v.getX());
        this.setMotionY(v.getY());
        this.setMotionZ(v.getZ());
    }

    public void setMotionX(double value) {
        this.write("f", (int) Math.floor(value * 8000.0D));
    }

    public double getMotionX() {
        return (((Integer) this.read("f")) / 8000.0D);
    }

    public void setMotionY(double value) {
        this.write("g", (int) Math.floor(value * 8000.0D));
    }

    public double getMotionY() {
        return (((Integer) this.read("g")) / 8000.0D);
    }

    public void setMotionZ(double value) {
        this.write("h", (int) Math.floor(value * 8000.0D));
    }

    public double getMotionZ() {
        return (((Integer) this.read("h")) / 8000.0D);
    }

    public void setMetadata(WrappedDataWatcher metadata) {
        this.write(HoloAPI.getPlugin().isUsingNetty ? "l" : "t", metadata.getHandle());
    }

    public Object getMetadata() {
        return this.read(HoloAPI.getPlugin().isUsingNetty ? "l" : "t");
    }
}