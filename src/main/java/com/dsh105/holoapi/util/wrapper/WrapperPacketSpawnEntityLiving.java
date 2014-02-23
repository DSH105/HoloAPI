package com.dsh105.holoapi.util.wrapper;

import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;
import net.minecraft.server.v1_7_R1.*;
import net.minecraft.server.v1_7_R1.DataWatcher;
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

    public void setX(int value) {
        this.write("c", (int) Math.floor(value * 32.0D));
    }

    public int getX() {
        return (int) (((Integer) this.read("c")) / 32.0D);
    }

    public void setY(int value) {
        this.write("d", (int) Math.floor(value * 32.0D));
    }

    public int getY() {
        return (int) (((Integer) this.read("d")) / 32.0D);
    }

    public void setZ(int value) {
        this.write("e", (int) Math.floor(value * 32.0D));
    }

    public int getZ() {
        return (int) (((Integer) this.read("e")) / 32.0D);
    }

    public void setYaw(float value) {
        this.write("i", (int) (value * 256.0F / 360.0F));
    }

    public float getYaw() {
        return (int) (((Integer) this.read("i")) * 360.0F / 256.0F);
    }

    public void setHeadPitch(float value) {
        this.write("j", (int) (value * 256.0F / 360.0F));
    }

    public float getHeadPitch() {
        return (int) (((Integer) this.read("j")) * 360.0F / 256.0F);
    }

    public void setHeadYaw(float value) {
        this.write("k", (int) (value * 256.0F / 360.0F));
    }

    public float getHeadYaw() {
        return (int) (((Integer) this.read("k")) * 360.0F / 256.0F);
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

    public void setDataWatcher(net.minecraft.server.v1_7_R1.DataWatcher dataWatcher) {
        this.write("l", dataWatcher);
    }

    public DataWatcher getData() {
        return (DataWatcher) this.read("l");
    }
}