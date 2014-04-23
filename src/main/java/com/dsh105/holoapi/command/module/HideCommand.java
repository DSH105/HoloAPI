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
import com.dsh105.holoapi.util.PlayerIdent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 3) {
            Hologram hologram = HoloAPI.getManager().getHologram(args[1]);
            if (hologram == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                return true;
            }
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                Lang.sendTo(sender, Lang.NULL_PLAYER.getValue().replace("%player%", args[1]));
                return true;
            }

            if (!hologram.getPlayerViews().keySet().contains(PlayerIdent.getIdentificationForAsString(target))) {
                Lang.sendTo(sender, Lang.HOLOGRAM_ALREADY_NOT_SEE.getValue().replace("%id%", args[1]).replace("%player%", args[1]));
                return true;
            }
            hologram.clear(target);
            Lang.sendTo(sender, Lang.HOLOGRAM_HIDE.getValue().replace("%id%", args[1]).replace("%player%", args[1]));
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<id> <player>", this.getPermission(), "Hide a Hologram from a player's view")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.hide");
    }
}