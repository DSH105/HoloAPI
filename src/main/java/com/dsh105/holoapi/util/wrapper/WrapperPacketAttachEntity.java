package com.dsh105.holoapi.util.wrapper;

import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;

public class WrapperPacketAttachEntity extends Packet {

    public WrapperPacketAttachEntity() {
        super(PacketFactory.PacketType.ENTITY_ATTACH);
    }

    public void setLeached(boolean flag) {
        this.write("a", flag ? 1 : 0);
    }

    public boolean getLeached() {
        return (Integer) this.read("a") != 0;
    }

    public void setEntityId(int value) {
        this.write("b", value);
    }

    public int getEntityId() {
        return (Integer) this.read("b");
    }

    public void setVehicleId(int value) {
        this.write("c", value);
    }

    public int getVehicleId() {
        return (Integer) this.read("c");
    }
}