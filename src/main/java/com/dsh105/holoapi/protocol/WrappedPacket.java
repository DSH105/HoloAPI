package com.dsh105.holoapi.protocol;

import com.captainbern.minecraft.protocol.PacketType;
import com.captainbern.minecraft.protocol.Protocol;
import com.captainbern.minecraft.protocol.Sender;
import com.captainbern.reflection.ClassTemplate;
import com.captainbern.reflection.Reflection;
import com.dsh105.holoapi.util.ClassModifier;

public class WrappedPacket {

    private Object handle;

    private ClassTemplate packetTemplate;

    private PacketType packetType;

    protected ClassModifier modifier;

    public WrappedPacket(PacketType packetType) {
        this(packetType, new Reflection().reflect(packetType.getPacketClass()).newInstance());  // Assume that it worked
    }

    public WrappedPacket(PacketType type, Object packetHandle) {
        this(type, packetHandle, new Reflection().reflect(packetHandle.getClass()));
    }

    public WrappedPacket(PacketType type, Object packetHandle, ClassTemplate classTemplate) {
         this(type, packetHandle, classTemplate, new ClassModifier(packetHandle, classTemplate));
    }

    public WrappedPacket(PacketType packetType, Object packetHandle, ClassTemplate packetTemplate, ClassModifier classModifier) {
        if (packetType == null)
            throw new IllegalArgumentException("PacketType can't be NULL!");
        if (packetHandle == null)
            throw new IllegalArgumentException("Packet handle can't be NULL!");
        if (packetTemplate == null)
            throw new IllegalArgumentException("Packet template can't be NULL!");
        if (classModifier == null)
            throw new IllegalArgumentException("ClassModifier can't be NULL!");

        this.packetType = packetType;
        this.handle = packetHandle;
        this.modifier = classModifier;
        this.packetTemplate = packetTemplate;
    }

    public static WrappedPacket fromPacket(Object packet) {
        PacketType type = PacketType.getTypeFrom(packet.getClass());
        return new WrappedPacket(type, packet);
    }

    public Object getHandle() {
        return this.handle;
    }

    public PacketType getPacketType() {
        if (this.packetType == null) {
            if (this.handle == null)
                throw new RuntimeException("Cannot return a valid PacketType!");

            try {
                this.packetType = PacketType.getTypeFrom(this.handle.getClass());
            } catch (Exception e) {
                // Swallow
            }

            if (this.packetType == null)
                throw new RuntimeException("Failed to retrieve the PacketType of: " + this.handle);
        }

        return this.packetType;
    }

    public int getId() {
        return getPacketType().getId();
    }

    public Sender getSender() {
        return getPacketType().getSender();
    }

    public Protocol getProtocol() {
        return getPacketType().getProtocol();
    }

    public ClassTemplate asClassTemplate() {
        if (this.packetTemplate == null) {
            if (this.handle != null) {
                this.packetTemplate = new Reflection().reflect(this.handle.getClass());
            } else {
                this.packetTemplate = new Reflection().reflect(getPacketType().getPacketClass());
            }
        }

        return this.packetTemplate;
    }

    public ClassModifier<Object> getModifier() {
        if (this.modifier == null)
            throw new RuntimeException("ClassModifier is NULL!");

        return this.modifier;
    }

    public <T> ClassModifier<T> getModifier(Class<T> type) {
        return this.modifier.withType(type);
    }

    public ClassModifier<Byte> getBytes() {
        return getModifier(byte.class);
    }

    public ClassModifier<Integer> getIntegers() {
        return getModifier(int.class);
    }

    public ClassModifier<Double> getDoubles() {
        return getModifier(double.class);
    }

    public ClassModifier<Long> getLongs() {
        return getModifier(long.class);
    }

    public ClassModifier<Short> getShorts() {
        return getModifier(short.class);
    }

    public ClassModifier<Float> getFloats() {
        return getModifier(float.class);
    }

    public ClassModifier<Boolean> getBooleans() {
        return getModifier(boolean.class);
    }

    public ClassModifier<String> getStrings() {
        return getModifier(String.class);
    }

    public ClassModifier<byte[]> getByteArrays() {
        return getModifier(byte[].class);
    }

    public ClassModifier<int[]> getIntegerArrays() {
        return getModifier(int[].class);
    }

    public ClassModifier<String[]> getStringArrays() {
        return getModifier(String[].class);
    }
}
