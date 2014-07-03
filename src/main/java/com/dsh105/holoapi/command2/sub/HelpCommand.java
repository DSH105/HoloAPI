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
import com.dsh105.commodus.GeneralUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.config.Lang;

public class HelpCommand implements CommandListener {

    @Command(
            command = "help",
            description = "Retrieve help for all HoloAPI commands"
    )
    public boolean help(CommandEvent event) {
        HoloAPI.getCommandManager().getHelpService().sendPage(event.sender(), 1);
        return true;
    }

    @Command(
            command = "help <index>",
            description = "Retrieve a certain help page of all HoloAPI commands"
    )
    public boolean helpPage(CommandEvent event) {
        try {
            HoloAPI.getCommandManager().getHelpService().sendPage(event.sender(), GeneralUtil.toInteger(event.variable("index")));
        } catch (NumberFormatException e) {
            event.respond(Lang.HELP_INDEX_TOO_BIG.getValue("index", event.variable("index")));
        }
        return true;
    }
}