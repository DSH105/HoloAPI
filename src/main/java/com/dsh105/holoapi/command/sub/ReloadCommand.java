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
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.config.ConfigType;
import com.dsh105.holoapi.config.Lang;

public class ReloadCommand implements CommandListener {

    @Command(
            command = "reload",
            description = "Reload all HoloAPI configuration files and holograms",
            permission = "holoapi.holo.reload"
    )
    public boolean command(CommandEvent event) {
        // Reload config files
        HoloAPI.getConfig(ConfigType.MAIN).reloadConfig();
        HoloAPI.getConfig(ConfigType.DATA).reloadConfig();
        HoloAPI.getConfig(ConfigType.LANG).reloadConfig();

        event.respond(Lang.CONFIGS_RELOADED.getValue());
        event.respond(Lang.HOLOGRAM_RELOAD.getValue());

        // Reload all configuration values
        HoloAPI.getCore().loadConfiguration();

        // Load all holograms
        HoloAPI.getCore().loadHolograms();
        return true;
    }
}