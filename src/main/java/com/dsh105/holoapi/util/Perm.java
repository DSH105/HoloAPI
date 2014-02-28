package com.dsh105.holoapi.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public enum Perm {

    UPDATE("holoapi.update"),
    RELOAD("holoapi.holo.reload"),
    CREATE("holoapi.holo.create"),
    REMOVE("holoapi.holo.remove"),
    INFO("holoapi.holo.info"),
    MOVE("holoapi.holo.move"),
    TELEPORT("holoapi.holo.teleport"),
    BUILD("holoapi.holo.build");

    String perm;

    Perm(String perm) {
        this.perm = perm;
    }

    public boolean hasPerm(CommandSender sender, boolean sendMessage, boolean allowConsole) {
        if (sender instanceof Player) {
            return hasPerm(((Player) sender), sendMessage);
        } else {
            if (!allowConsole && sendMessage) {
                Lang.sendTo(sender, Lang.IN_GAME_ONLY.toString());
            }
            return allowConsole;
        }
    }

    public boolean hasPerm(Player player, boolean sendMessage) {
        if (player.hasPermission(this.perm)) {
            return true;
        }
        if (sendMessage) {
            Lang.sendTo(player, Lang.NO_PERMISSION.toString().replace("%perm%", this.perm));
        }
        return false;
    }
}