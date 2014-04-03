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

import org.bukkit.Bukkit;

public class MCPCPlusServer extends SpigotServer {

    @Override
    public boolean init() {
        if (!super.init() || !Bukkit.getServer().getVersion().contains("MCPC-Plus")) {
            return false;
        }

        this.MINECRAFT_VERSIONED = "net.minecraft.server";  // in mcpc+ there is no versioning :/

        return true;
    }

    @Override
    public String getName() {
        return "MCPC+";
    }

    @Override
    public ServerBrand getServerBrand() {
        return ServerBrand.MCPC_PLUS;
    }
}
