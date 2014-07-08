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
import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.util.MiscUtil;
import org.bukkit.Location;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ClearCommand implements CommandListener {

    private static final List<String> VALID_TYPES = Arrays.asList("all", "complex", "simple");

    @Command(
            command = "clear",
            description = "Clear all holograms (simple holograms not included)",
            permission = "holoapi.holo.clear"
    )
    public boolean command(CommandEvent event) {
        clear("ALL");
        event.respond(Lang.COMPLEX_HOLOGRAMS_CLEARED.getValue());
        return true;
    }

    @Command(
            command = "clear <type>",
            description = "Clear all holograms of a certain type",
            permission = "holoapi.holo.clear",
            help = "Valid types are: COMPLEX, SIMPLE, ALL"
    )
    public boolean clearType(CommandEvent event) {
        if (!VALID_TYPES.contains(event.variable("type").toLowerCase())) {
            event.respond(Lang.INVALID_CLEAR_TYPE.getValue("type", event.variable("type"), "valid", StringUtil.combine(", ", VALID_TYPES)));
            return true;
        }
        clear(event.variable("type"));
        if (event.variable("type").equalsIgnoreCase("COMPLEX")) {
            event.respond(Lang.COMPLEX_HOLOGRAMS_CLEARED.getValue());
        } else if (event.variable("type").equalsIgnoreCase("SIMPLE")) {
            event.respond(Lang.SIMPLE_HOLOGRAMS_CLEARED.getValue());
        } else {
            event.respond(Lang.ALL_HOLOGRAMS_CLEARED.getValue());
        }
        return true;
    }

    private void clear(String type) {
        Iterator<Hologram> i = HoloAPI.getManager().getAllComplexHolograms().keySet().iterator();
        while (i.hasNext()) {
            Hologram h = i.next();
            if (type.equalsIgnoreCase("COMPLEX") && h.isSimple()) {
                continue;
            }
            if (type.equalsIgnoreCase("SIMPLE") && !h.isSimple()) {
                continue;
            }

            if (!h.isSimple()) {
                HoloAPI.getManager().saveToFile(h);
            }
            h.clearAllPlayerViews();
            i.remove();
        }
    }
}