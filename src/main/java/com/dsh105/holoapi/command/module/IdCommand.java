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
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.command.CommandSender;

public class IdCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("set")) {
                String oldId = args[2];
                String newId = args[2];
                Hologram h = HoloAPI.getManager().getHologram(oldId);
                if (h == null) {
                    Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", oldId));
                    return true;
                }

                if (HoloAPI.getConfig(HoloAPI.ConfigType.DATA).getConfigurationSection("holograms." + newId) != null) {
                    Lang.sendTo(sender, Lang.HOLOGRAM_DUPLICATE_ID.getValue().replace("%id%", newId));
                    return true;
                }

                h.setSaveId(newId);
                Lang.sendTo(sender, Lang.HOLOGRAM_SET_ID.getValue().replace("%oldid%", oldId).replace("%newid%", newId));
                return true;
            }
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[] {
                new CommandHelp(this, "set <old_id> <new_id>", this.getPermission(), "Set the save ID of an existing hologram.", "Save IDs are used to identify holograms and are used in the HoloAPI save files")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.saveid");
    }
}