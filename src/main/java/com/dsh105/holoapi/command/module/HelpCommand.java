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

package com.dsh105.holoapi.command.module;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import com.dsh105.holoapi.util.StringUtil;
import org.bukkit.command.CommandSender;

public class HelpCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            HoloAPI.getCommandManager().sendHelpTo(sender);
            return true;
        } else if (args.length == 2) {
            if (StringUtil.isInt(args[1])) {
                HoloAPI.getCommandManager().sendHelpTo(sender, Integer.parseInt(args[1]));
                return true;
            } else {
                HoloAPI.getCommandManager().sendHelpTo(sender, args[1]);
                return true;
            }
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "Retrieve help for all HoloAPI commands."),
                new CommandHelp(this, "<page>", (Permission) null, "See the help page of a certain number."),
                new CommandHelp(this, "<command>", (Permission) null, "Retrieve help for a certain command.")
        };
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}