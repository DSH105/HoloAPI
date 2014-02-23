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

    public void setX(int value) {
        this.write("b", (int) Math.floor(value * 32.0D));
    }

    public int getX() {
        return (int) (((Integer) this.read("b")) / 32.0D);
    }

    public void setY(int value) {
        this.write("c", (int) Math.floor(value * 32.0D));
    }

    public int getY() {
        return (int) (((Integer) this.read("c")) / 32.0D);
    }

    public void setZ(int value) {
        this.write("d", (int) Math.floor(value * 32.0D));
    }

    public int getZ() {
        return (int) (((Integer) this.read("d")) / 32.0D);
    }

    public void setMotionX(int value) {
        this.write("e", (int) Math.floor(value * 8000.0D));
    }

    public int getMotionX() {
        return (int) (((Integer) this.read("e")) / 8000.0D);
    }

    public void setMotionY(int value) {
        this.write("f", (int) Math.floor(value * 8000.0D));
    }

    public int getMotionY() {
        return (int) (((Integer) this.read("f")) / 8000.0D);
    }

    public void setMotionZ(int value) {
        this.write("g", (int) Math.floor(value * 8000.0D));
    }

    public int getMotionZ() {
        return (int) (((Integer) this.read("g")) / 8000.0D);
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