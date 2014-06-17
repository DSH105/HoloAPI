package com.dsh105.holoapi.protocol;

import com.captainbern.minecraft.conversion.BukkitConverters;
import com.captainbern.minecraft.protocol.PacketType;
import com.captainbern.minecraft.protocol.Protocol;
import com.captainbern.minecraft.protocol.Sender;
import com.captainbern.minecraft.reflection.MinecraftReflection;
import com.captainbern.minecraft.reflection.utils.Accessor;
import com.captainbern.minecraft.wrapper.WrappedDataWatcher;
import com.captainbern.reflection.ClassTemplate;
import com.captainbern.reflection.Reflection;

public class WrappedPacket {

    private Object handle;

    private ClassTemplate packetTemplate;

    private PacketType packetType;

    protected Accessor modifier;

    public WrappedPacket(PacketType packetType) {
        this(packetType, new Reflection().reflect(packetType.getPacketClass()).newInstance());  // Assume that it worked
    }

    public WrappedPacket(PacketType type, Object packetHandle) {
        this(type, packetHandle, new Reflection().reflect(packetHandle.getClass()));
    }

    public WrappedPacket(PacketType type, Object packetHandle, ClassTemplate classTemplate) {
         this(type, packetHandle, classTemplate, new Accessor(packetHandle, classTemplate));
    }

    public WrappedPacket(PacketType packetType, Object packetHandle, ClassTemplate packetTemplate, Accessor classModifier) {
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

    public Accessor getAccessor() {
        if (this.modifier == null)
            throw new RuntimeException("Accessor is NULL!");

        return this.modifier;
    }

    public <T> Accessor<T> getAccessor(Class<T> type) {
        return this.modifier.withType(type);
    }

    public Accessor<Byte> getBytes() {
        return getAccessor(byte.class);
    }

    public Accessor<Integer> getIntegers() {
        return getAccessor(int.class);
    }

    public Accessor<Double> getDoubles() {
        return getAccessor(double.class);
    }

    public Accessor<Long> getLongs() {
        return getAccessor(long.class);
    }

    public Accessor<Short> getShorts() {
        return getAccessor(short.class);
    }

    public Accessor<Float> getFloats() {
        return getAccessor(float.class);
    }

    public Accessor<Boolean> getBooleans() {
        return getAccessor(boolean.class);
    }

    public Accessor<String> getStrings() {
        return getAccessor(String.class);
    }

    public Accessor<byte[]> getByteArrays() {
        return getAccessor(byte[].class);
    }

    public Accessor<int[]> getIntegerArrays() {
        return getAccessor(int[].class);
    }

    public Accessor<String[]> getStringArrays() {
        return getAccessor(String[].class);
    }

    public Accessor<WrappedDataWatcher> getDataWatchers() {
        return this.modifier.withType(MinecraftReflection.getDataWatcherClass(), BukkitConverters.getDataWatcherConverter());
    }
}
