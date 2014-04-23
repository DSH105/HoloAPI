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
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.LocationFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.MiscUtil;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class CopyCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 2 || args.length == 6) {
            final Hologram hologram = HoloAPI.getManager().getHologram(args[1]);
            if (hologram == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                return true;
            }
            Location location;
            if (args.length == 6) {
                location = MiscUtil.getLocationFrom(args, 2);
                if (location == null) {
                    Lang.sendTo(sender, Lang.NOT_LOCATION.getValue());
                    return true;
                }
            } else {
                if (sender instanceof Player) {
                    location = ((Player) sender).getLocation();
                } else {
                    InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
                        Hologram copy;

                        @Override
                        public void onFunction(ConversationContext context, String input) {
                            copy = HoloAPI.getManager().copy(hologram, this.getLocation());
                        }

                        @Override
                        public String getSuccessMessage(ConversationContext context, String input) {
                            if (copy instanceof AnimatedHologram) {
                                return Lang.HOLOGRAM_ANIMATED_COPIED.getValue().replace("%id%", copy.getSaveId());
                            } else {
                                return Lang.HOLOGRAM_COPIED.getValue().replace("%id%", copy.getSaveId());
                            }
                        }
                    }));
                    return true;
                }
            }
            Hologram copy = HoloAPI.getManager().copy(hologram, location);
            if (copy instanceof AnimatedHologram) {
                Lang.sendTo(sender, Lang.HOLOGRAM_ANIMATED_COPIED.getValue().replace("%id%", hologram.getSaveId()));
            } else {
                Lang.sendTo(sender, Lang.HOLOGRAM_COPIED.getValue().replace("%id%", hologram.getSaveId()));
            }
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<id>", this.getPermission(), "Copy a hologram to your current position."),
                new CommandHelp(this, "<id> <world> <x> <y> <z>", this.getPermission(), "Copy a hologram to the specified permission.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.copy");
    }
}