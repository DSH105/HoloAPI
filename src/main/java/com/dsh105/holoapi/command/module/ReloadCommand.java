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
import org.bukkit.command.CommandSender;

public class ReloadCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // Reload config files
            HoloAPI.getConfig(HoloAPI.ConfigType.MAIN).reloadConfig();
            HoloAPI.getConfig(HoloAPI.ConfigType.DATA).reloadConfig();
            HoloAPI.getConfig(HoloAPI.ConfigType.LANG).reloadConfig();

            // Reload all configuration values
            HoloAPI.getCore().loadConfiguration();

            // Load all holograms
            HoloAPI.getCore().loadHolograms();

            Lang.sendTo(sender, Lang.CONFIGS_RELOADED.getValue());
            Lang.sendTo(sender, Lang.HOLOGRAM_RELOAD.getValue());
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "Reload all HoloAPI configuration files and holograms.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.reload");
    }
}