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
    BUILD("holoapi.holo.build"),
    EDIT("holoapi.holo.edit"),
    REFRESH("holoapi.holo.refresh"),
    COPY("holoapi.holo.copy"),
    NEARBY("holoapi.holo.nearby"),
    ADDLINE("holoapi.holo.addline"),

    TOUCH_ADD("holoapi.holo.touch.add"),
    TOUCH_REMOVE("holoapi.holo.touch.remove"),
    TOUCH_CLEAR("holoapi.holo.touch.clear"),
    TOUCH_INFO("holoapi.holo.touch.info");

    String perm;

    Perm(String perm) {
        this.perm = perm;
    }

    public String getPermission() {
        return perm;
    }

    public boolean hasPerm(CommandSender sender, boolean sendMessage, boolean allowConsole) {
        if (sender instanceof Player) {
            return hasPerm(((Player) sender), sendMessage);
        } else {
            if (!allowConsole && sendMessage) {
                Lang.sendTo(sender, Lang.IN_GAME_ONLY.getValue());
            }
            return allowConsole;
        }
    }

    public boolean hasPerm(Player player, boolean sendMessage) {
        if (player.hasPermission(this.perm)) {
            return true;
        }
        if (sendMessage) {
            Lang.sendTo(player, Lang.NO_PERMISSION.getValue().replace("%perm%", this.perm));
        }
        return false;
    }
}