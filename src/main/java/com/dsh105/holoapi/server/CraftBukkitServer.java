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

package com.dsh105.holoapi.server;

import com.dsh105.holoapi.reflection.refs.MinecraftServerRef;
import org.bukkit.Bukkit;

public class CraftBukkitServer implements Server {

    public String MC_VERSION;

    public String CRAFTBUKKIT_VERSIONED;

    public String MINECRAFT_VERSIONED;

    @Override
    public boolean init() {
        String serverPath = Bukkit.getServer().getClass().getName();

        if (!serverPath.startsWith("org.bukkit.craftbukkit")) {
            return false;
        }

        Class<?> craftServer = Bukkit.getServer().getClass();

        if(craftServer != null) {

            MC_VERSION = trimPackageName(craftServer.getCanonicalName());

            if (MC_VERSION.isEmpty()) {
                CRAFTBUKKIT_VERSIONED = "org.bukkit.craftbukkit";
                MINECRAFT_VERSIONED = "net.minecraft.server";
            } else {
                CRAFTBUKKIT_VERSIONED = "org.bukkit.craftbukkit." + MC_VERSION;
                MINECRAFT_VERSIONED = "net.minecraft.server." + MC_VERSION;
            }

        } else {
            throw new RuntimeException("Bukkit not found!");
        }

        return true;
    }

    @Override
    public boolean postInit() {
        return false;
    }

    private static String trimPackageName(String packageName) {
        int index = packageName.lastIndexOf('.');

        if(index > 0) {
            return packageName.substring(0, index);
        } else {
            return "<unknown>";
        }
    }

    @Override
    public String getName() {
        return "CraftBukkit";
    }

    @Override
    public Object getMinecraftServer() {
        return MinecraftServerRef.getServer(Bukkit.getServer());
    }

    @Override
    public String getMCVersion() {
        return MC_VERSION;
    }

    @Override
    public boolean isCompatible() {
        return true;
    }

    @Override
    public ServerBrand getServerBrand() {
        return ServerBrand.CRAFTBUKKIT;
    }
}
