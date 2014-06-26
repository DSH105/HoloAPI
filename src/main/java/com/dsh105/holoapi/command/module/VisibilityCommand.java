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

import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.command.CommandSender;

public class VisibilityCommand extends CommandModule {

    Permission basePerm = new Permission("holoapi.holo.visibility");

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            Hologram hologram = HoloAPI.getManager().getHologram(args[1]);
            if (hologram == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                return true;
            }
            String visKey = HoloAPI.getVisibilityMatcher().getKeyOf(hologram.getVisibility());
            Visibility visibility = HoloAPI.getVisibilityMatcher().get(visKey);
            if (visibility == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_VISIBILITY_UNREGISTERED.getValue().replace("%id%", hologram.getSaveId()));
            } else {
                Lang.sendTo(sender, Lang.HOLOGRAM_VISIBILITY.getValue().replace("%id%", hologram.getSaveId()).replace("%visibility%", visKey));
            }
            return true;
        } else if (args.length == 3) {
            if (new Permission(this.basePerm, "set").hasPerm(sender, true, true)) {
                Hologram hologram = HoloAPI.getManager().getHologram(args[1]);
                if (hologram == null) {
                    Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                    return true;
                }
                Visibility visibility = HoloAPI.getVisibilityMatcher().get(args[2].toLowerCase());
                if (visibility == null) {
                    Lang.sendTo(sender, Lang.INVALID_VISIBILITY.getValue().replace("%visibility%", args[2].toLowerCase()));
                    Lang.sendTo(sender, Lang.VALID_VISIBILITIES.getValue().replace("%vis%", StringUtil.join(HoloAPI.getVisibilityMatcher().getValidVisibilities().keySet(), ", ")));
                    return true;
                }
                hologram.setVisibility(visibility);
                Lang.sendTo(sender, Lang.HOLOGRAM_VISIBILITY_SET.getValue().replace("%id%", args[1]).replace("%visibility%", args[2].toLowerCase()));
                return true;
            } else return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<id>", this.basePerm, "View the visibility of a particular hologram."),
                new CommandHelp(this, "<id> <type>", new Permission(this.basePerm, "set"), "Set the visibility of a particular hologram.", "Valid types for HoloAPI are: all, permission.", "Visibility types dynamically registered using the API may be defined using this command.")
        };
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}