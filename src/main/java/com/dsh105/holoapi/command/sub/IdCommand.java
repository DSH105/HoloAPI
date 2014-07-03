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

package com.dsh105.holoapi.command.sub;

import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.ConfigType;
import com.dsh105.holoapi.config.Lang;

public class IdCommand implements CommandListener {

    @Command(
            command = "id set <old_id> <new_id>",
            description = "Set the save ID of an existing hologram",
            permission = "holoapi.holo.saveid",
            help = "Save IDs are used to identify holograms and are used in the HoloAPI save files"
    )
    public boolean command(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("old_id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("old_id")));
            return true;
        }

        if (HoloAPI.getConfig(ConfigType.DATA).getConfigurationSection("holograms." + event.variable("new_id")) != null) {
            event.respond(Lang.HOLOGRAM_DUPLICATE_ID.getValue("id", event.variable("new_id")));
            return true;
        }
        hologram.setSaveId(event.variable("new_id"));
        event.respond(Lang.HOLOGRAM_SET_ID.getValue("oldid", event.variable("old_id"), "newid", event.variable("new_id")));
        return true;
    }
}