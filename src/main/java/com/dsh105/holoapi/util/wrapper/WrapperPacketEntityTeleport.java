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
        this.write("b", Math.floor(value * 32.0D));
    }

    public double getX() {
        return (((Integer) this.read("b")) / 32.0D);
    }

    public void setY(double value) {
        this.write("c", Math.floor(value * 32.0D));
    }

    public double getY() {
        return (((Integer) this.read("c")) / 32.0D);
    }

    public void setZ(double value) {
        this.write("d", Math.floor(value * 32.0D));
    }

    public double getZ() {
        return (((Integer) this.read("d")) / 32.0D);
    }

    public void setYaw(float value) {
        this.write("e", (value * 256.0F / 360.0F));
    }

    public float getYaw() {
        return (((Byte) this.read("e")) * 360.0F / 256.0F);
    }

    public void setPitch(float value) {
        this.write("f", (value * 256.0F / 360.0F));
    }

    public float getPitch() {
        return (((Byte) this.read("f")) * 360.0F / 256.0F);
    }
}