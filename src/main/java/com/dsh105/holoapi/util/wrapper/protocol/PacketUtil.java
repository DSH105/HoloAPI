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

package com.dsh105.holoapi.util.wrapper.protocol;

import com.dsh105.holoapi.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

public class PacketUtil {

    public static final Class CLASS_TEMPLATE = ReflectionUtil.getNMSClass("EnumProtocol");
    private static final Field SERVER_PACKET_MAP = ReflectionUtil.getField(CLASS_TEMPLATE, "i");
    private static final Field CLIENT_PACKET_MAP = ReflectionUtil.getField(CLASS_TEMPLATE, "h");

    public static Class getPacket(Protocol protocol, Sender sender, int id) {
        if (sender == Sender.CLIENT) {
            try {
                return (Class) ((Map) CLIENT_PACKET_MAP.get(protocol.toVanilla())).get(id);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (sender == Sender.SERVER) {
            try {
                return (Class) ((Map) SERVER_PACKET_MAP.get(protocol.toVanilla())).get(id);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
