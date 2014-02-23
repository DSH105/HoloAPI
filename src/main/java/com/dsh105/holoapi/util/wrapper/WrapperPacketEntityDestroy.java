package com.dsh105.holoapi.util.wrapper;

import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;

import java.util.Arrays;
import java.util.List;

public class WrapperPacketEntityDestroy extends Packet {

    public WrapperPacketEntityDestroy() {
        super(PacketFactory.PacketType.ENTITY_DESTROY);
    }

    public void setEntities(int[] value) {
        this.write("a", value);
    }

    public List<Integer> getEntities() {
        return Arrays.asList((Integer[]) this.read("a"));
    }
}