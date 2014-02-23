package com.dsh105.holoapi.util.wrapper;

import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;

public class WrapperPacketSpawnEntity extends Packet {

    public WrapperPacketSpawnEntity() {
        super(PacketFactory.PacketType.ENTITY_SPAWN);
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

    public void setMotionX(double value) {
        this.write("e", (int) Math.floor(value * 8000.0D));
    }

    public double getMotionX() {
        return (((Integer) this.read("e")) / 8000.0D);
    }

    public void setMotionY(double value) {
        this.write("f", (int) Math.floor(value * 8000.0D));
    }

    public double getMotionY() {
        return (int) (((Integer) this.read("f")) / 8000.0D);
    }

    public void setMotionZ(double value) {
        this.write("g", (int) Math.floor(value * 8000.0D));
    }

    public double getMotionZ() {
        return (((Integer) this.read("g")) / 8000.0D);
    }

    public void setYaw(float value) {
        this.write("h", (int) (value * 256.0F / 360.0F));
    }

    public float getYaw() {
        return (int) (((Integer) this.read("h")) * 360.0F / 256.0F);
    }

    public void setPitch(float value) {
        this.write("i", (int) (value * 256.0F / 360.0F));
    }

    public float getPitch() {
        return (int) (((Integer) this.read("i")) * 360.0F / 256.0F);
    }

    public void setEntityType(int value) {
        this.write("j", value);
    }

    public int getEntityType() {
        return (Integer) this.read("j");
    }

    public void setData(int value) {
        this.write("k", value);
    }

    public int getData() {
        return (Integer) this.read("k");
    }
}