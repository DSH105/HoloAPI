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

import org.bukkit.command.CommandSender;

public abstract class CommandModule {

    private String commandArguments;

    public String getCommandArguments() {
        return commandArguments;
    }

    public void setCommandArguments(String commandArguments) {
        this.commandArguments = commandArguments;
    }

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public abstract CommandHelp[] getHelp();

    public abstract String getPermission();
}