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

import com.captainbern.minecraft.reflection.MinecraftReflection;
import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.commodus.GeneralUtil;
import com.dsh105.commodus.GeometryUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Lang;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class NearbyCommand implements CommandListener {

    @Command(
            command = "nearby <radius>",
            description = "View information on all nearby holograms within the specified radius",
            permission = "holoapi.holo.nearby"
    )
    public boolean command(CommandEvent<Player> event) {
        int radius;
        try {
            radius = GeneralUtil.toInteger(event.variable("radius"));
        } catch (NumberFormatException e) {
            event.respond(Lang.INT_ONLY.getValue("string", event.variable("radius")));
            return true;
        }

        ArrayList<Hologram> nearby = new ArrayList<Hologram>();
        for (Hologram hologram : HoloAPI.getManager().getAllComplexHolograms().keySet()) {
            if (GeometryUtil.getNearbyEntities(hologram.getDefaultLocation(), radius).contains(event.sender())) {
                nearby.add(hologram);
            }
        }

        if (nearby.isEmpty()) {
            event.respond(Lang.NO_NEARBY_HOLOGRAMS.getValue("radius", radius));
            return true;
        }

        InfoCommand.info(event.sender(), nearby);
        if (MinecraftReflection.isUsingNetty()) {
            event.respond(Lang.TIP_HOVER_PREVIEW.getValue());
        }
        return true;
    }
}