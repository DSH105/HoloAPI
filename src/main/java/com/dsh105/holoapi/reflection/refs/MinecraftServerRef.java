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

package com.dsh105.holoapi.reflection.refs;

import com.dsh105.holoapi.reflection.CBClassTemplate;
import com.dsh105.holoapi.reflection.ClassTemplate;
import com.dsh105.holoapi.reflection.MethodAccessor;
import com.dsh105.holoapi.reflection.NMSClassTemplate;
import org.bukkit.Server;

public class MinecraftServerRef {

    public static final ClassTemplate CRAFTSERVER = CBClassTemplate.create("CraftServer");
    public static final MethodAccessor<Object> CRAFTSERVER_GET_HANDLE = CRAFTSERVER.getMethod("getHandle");

    public static final ClassTemplate MINECRAFTSERVER = NMSClassTemplate.create("MinecraftServer");
    public static final MethodAccessor MINECRAFTSERVER_GET_SERVER = MINECRAFTSERVER.getMethod("getServer");

    public static Object toCraftServer(Server server) {
        return CRAFTSERVER_GET_HANDLE.invoke(server);
    }

    public static Object getServer(Server server) {
        return MINECRAFTSERVER_GET_SERVER.invoke(toCraftServer(server));
    }
}
