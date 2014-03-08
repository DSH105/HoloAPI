package com.dsh105.holoapi.util.wrapper;

import com.dsh105.holoapi.reflection.SafeMethod;
import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;

public class WrapperPacketEntityMetadata extends Packet {

    public WrapperPacketEntityMetadata() {
        super(PacketFactory.PacketType.ENTITY_METADATA);
    }

    public void setEntityId(int value) {
        this.write("a", value);
    }

    public int getEntityId() {
        return (Integer) this.read("a");
    }

    public void setMetadata(WrappedDataWatcher metadata) {
        Object handle = metadata.getHandle();
        this.write("b", new SafeMethod<Void>(handle.getClass(), "c").invoke(handle));
    }
}