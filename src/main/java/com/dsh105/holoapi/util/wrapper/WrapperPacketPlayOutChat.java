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

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.reflection.SafeMethod;
import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.ReflectionUtil;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;

public class WrapperPacketPlayOutChat extends Packet {

    public WrapperPacketPlayOutChat() {
        super(PacketFactory.PacketType.CHAT);
    }

    public void setMessage(String chatComponent) {
        if (!HoloAPI.isUsingNetty) {
            this.write("message", chatComponent);
            return;
        }
        this.write("a", new SafeMethod(ReflectionUtil.getNMSClass("ChatSerializer"), "a", String.class).invoke(null, chatComponent));
    }

    public String getMessage() {
        if (!HoloAPI.isUsingNetty) {
            return (String) this.read("message");
        }
        return (String) new SafeMethod(ReflectionUtil.getNMSClass("ChatSerializer"), "a", ReflectionUtil.getNMSClass("IChatBaseComponent")).invoke(null, this.read("a"));
    }
}