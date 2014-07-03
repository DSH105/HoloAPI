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

package com.dsh105.holoapi.command;

import com.captainbern.minecraft.reflection.MinecraftReflection;
import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.commodus.GeneralUtil;
import com.dsh105.holoapi.HoloAPI;
import org.bukkit.Bukkit;

public class HoloDebugCommand implements CommandListener {

    private static String[] messages = {"A cow ain't a pig!", "Leave flappy bird alone!", "I don't like this",
            "Stop spamming", "Goodmorning America!", "DSH105 is sexy!", "PressHearthToContinue",
            "Stay away from my budder!", "Wot", "If HoloAPI works then DSH105 and CaptainBern wrote it, else I have no idea who wrote that crap"};
    
    @Command(
            command = "holodebug",
            description = "Smashing bugs and coloring books",
            permission = "holo.debug"
    )
    public boolean debug(CommandEvent event) {
        event.respond("Debug results: ");
        event.respond("--------------[HoloAPI Stuff]--------------");
        event.respond("HoloAPI-Version: " + HoloAPI.getCore().getDescription().getVersion());
        event.respond("Message of the day: " + messages[GeneralUtil.random().nextInt(messages.length)]);
        event.respond("--------------[CraftBukkit Stuff]--------------");
        event.respond("Version tag: " + MinecraftReflection.getVersionTag());
        event.respond("NMS-package: " + MinecraftReflection.getMinecraftPackage());
        event.respond("CB-package: " + MinecraftReflection.getCraftBukkitPackage());
        event.respond("Bukkit version: " + Bukkit.getBukkitVersion());
        event.respond("--------------[Minecraft Server Stuff]--------------");
        event.respond("Using Netty: " + MinecraftReflection.isUsingNetty());
        event.respond("MinecraftServer: " + MinecraftReflection.getMinecraftClass("MinecraftServer").getCanonicalName());
        event.respond("Entity: " + MinecraftReflection.getMinecraftClass("Entity"));
        event.respond("Is (forge) Modded: " + (MinecraftReflection.getMinecraftClass("Entity").getCanonicalName().startsWith("net.minecraft.entity") ? "Definitely" : "Probably not"));

        return true;
    }
}