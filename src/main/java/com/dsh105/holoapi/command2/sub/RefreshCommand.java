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

import java.util.List;
import java.util.Set;

public class RefreshCommand implements CommandListener {

    @Command(
            command = "refresh",
            description = "Refresh all holograms",
            permission = "holoapi.holo.refresh"
    )
    public boolean command(CommandEvent event) {
        Set<Hologram> holograms = HoloAPI.getManager().getAllComplexHolograms().keySet();
        if (holograms.isEmpty()) {
            event.respond(Lang.NO_ACTIVE_HOLOGRAMS.getValue());
            return true;
        }

        for (Hologram hologram : holograms) {
            hologram.refreshDisplay(true);
        }
        event.respond(Lang.HOLOGRAMS_REFRESHED.getValue());
        return true;
    }

    @Command(
            command = "refresh <id>",
            description = "Refresh a hologram",
            permission = "holoapi.holo.refresh"
    )
    public boolean withId(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        hologram.refreshDisplay(true);
        event.respond(Lang.HOLOGRAM_REFRESH.getValue("id", hologram.getSaveId()));
        return true;
    }
}