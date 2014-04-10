package com.dsh105.holoapi.command;

import com.dsh105.holoapi.reflection.utility.CommonReflection;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HoloDebugCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(!sender.hasPermission("holo.debug")) {
            sender.sendMessage(ChatColor.RED + "Stop bugging me...");
            return false;
        }

        sender.sendMessage("Debug results: ");
        sender.sendMessage("Using Netty: " + CommonReflection.isUsingNetty());
        sender.sendMessage("Version tag: " + CommonReflection.getVersionTag());
        sender.sendMessage("NMS-package: " + CommonReflection.getMinecraftPackage());
        sender.sendMessage("CB-package: " + CommonReflection.getCraftBukkitPackage());
        sender.sendMessage("--------------------------------------------------------");
        sender.sendMessage("MinecraftServer: " + CommonReflection.getMinecraftClass("MinecraftServer").getCanonicalName());

        return false;
    }
}
