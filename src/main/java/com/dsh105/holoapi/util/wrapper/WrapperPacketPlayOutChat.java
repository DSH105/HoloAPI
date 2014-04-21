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
import com.dsh105.holoapi.reflection.Constants;
import com.dsh105.holoapi.reflection.SafeMethod;
import com.dsh105.holoapi.util.PacketFactory;
import com.dsh105.holoapi.util.ReflectionUtil;
import com.dsh105.holoapi.util.wrapper.protocol.Packet;

public class WrapperPacketPlayOutChat extends Packet {

    public WrapperPacketPlayOutChat() {
        super(PacketFactory.PacketType.CHAT);
    }

    public void setMessage(String chatComponent) {
        if (HoloAPI.getCore().isUsingNetty) {
            this.write(Constants.PACKET_CHAT_FIELD_MESSAGE.getName(), new SafeMethod(ReflectionUtil.getNMSClass("ChatSerializer"), Constants.PACKET_CHAT_FUNC_SETCOMPONENT.getName(), String.class).invoke(null, chatComponent));
        } else {
            this.write(Constants.PACKET_CHAT_FIELD_MESSAGE.getName(), chatComponent);
        }
    }

    public String getMessage() {
        Object value = this.read(Constants.PACKET_CHAT_FIELD_MESSAGE.getName());
        if (value instanceof String) {
            return (String) value;
        }
        return (String) new SafeMethod(ReflectionUtil.getNMSClass("ChatSerializer"), Constants.PACKET_CHAT_FUNC_GETMESSAGE.getName(), ReflectionUtil.getNMSClass("IChatBaseComponent")).invoke(null, value);
    }
}