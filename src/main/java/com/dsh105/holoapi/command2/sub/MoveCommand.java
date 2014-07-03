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

package com.dsh105.holoapi.command2.sub;

import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.util.MiscUtil;
import org.bukkit.Location;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

public class MoveCommand implements CommandListener {

    @Command(
            command = "move <id>",
            description = "Move a hologram to your current position",
            permission = "holoapi.holo.move"
    )
    public boolean command(CommandEvent<Player> event) {
        final Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        hologram.move(event.sender().getLocation());
        event.respond(Lang.HOLOGRAM_MOVED.getValue());
        return true;
    }

    @Command(
            command = "move <id> <world> <x> <y> <z>",
            description = "Move a hologram to your current position",
            permission = "holoapi.holo.move"
    )
    public boolean moveLocation(CommandEvent<Player> event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        Location location = MiscUtil.getLocation(event);
        if (location == null) {
            return true;
        }

        hologram.move(location);
        event.respond(Lang.HOLOGRAM_MOVED.getValue());
        return true;
    }
}