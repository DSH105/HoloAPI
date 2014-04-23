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
import com.dsh105.holoapi.data.Updater;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.command.CommandSender;

public class UpdateCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (HoloAPI.getCore().updateChecked) {
            new Updater(HoloAPI.getCore(), 74914, HoloAPI.getCore().file, Updater.UpdateType.NO_VERSION_CHECK, true);
        } else {
            Lang.sendTo(sender, Lang.UPDATE_NOT_AVAILABLE.getValue());
        }
        return true;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "Update the plugin if a new release is available.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.update");
    }
}