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
import com.dsh105.commodus.data.Updater;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.config.Lang;

public class UpdateCommand implements CommandListener {

    @Command(
            command = "update",
            description = "Update the plugin if a new release is available",
            permission = "holoapi.update"
    )
    public boolean command(CommandEvent event) {
        if (HoloAPI.getCore().updateChecked) {
            new Updater(HoloAPI.getCore(), 74914, HoloAPI.getCore().file, Updater.UpdateType.NO_VERSION_CHECK, true);
        } else {
            event.respond(Lang.UPDATE_NOT_AVAILABLE.getValue());
        }
        return true;
    }
}