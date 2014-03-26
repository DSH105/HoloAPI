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