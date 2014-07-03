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
import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.config.Lang;

public class VisibilityCommand implements CommandListener {

    @Command(
            command = "visibility <id>",
            description = "Fetch the current visibility of a particular hologram",
            permission = "holoapi.holo.visibility"
    )
    public boolean command(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        String visibilityKey = HoloAPI.getVisibilityMatcher().getKeyOf(hologram.getVisibility());

        if (visibilityKey == null) {
            event.respond(Lang.HOLOGRAM_VISIBILITY_UNREGISTERED.getValue("id", hologram.getSaveId()));
        }
        event.respond(Lang.HOLOGRAM_VISIBILITY.getValue("id", hologram.getSaveId(), "visibility", visibilityKey));
        return true;
    }

    @Command(
            command = "visibility <id> <type>",
            description = "Set the visibility of a particular hologram",
            permission = "holoapi.holo.visibility.set",
            help = {"Valid types for HoloAPI are: all, permission.", "Visibility types dynamically registered using the API may be defined using this command.", "See the Wiki for more information"}
    )
    public boolean set(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        Visibility visibility = HoloAPI.getVisibilityMatcher().get(event.variable("type"));
        if (visibility == null) {
            event.respond(Lang.INVALID_VISIBILITY.getValue("visibility", event.variable("type")));
            event.respond(Lang.VALID_VISIBILITIES.getValue("vis", StringUtil.combine(", ", HoloAPI.getVisibilityMatcher().getValidVisibilities().keySet())));
            return true;
        }

        hologram.setVisibility(visibility);
        event.respond(Lang.HOLOGRAM_VISIBILITY_SET.getValue("id", event.variable("id"), "visibility", event.variable("type")));
        return true;
    }
}