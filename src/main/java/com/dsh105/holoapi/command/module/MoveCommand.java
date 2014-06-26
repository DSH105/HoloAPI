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

import com.dsh105.commodus.GeneralUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.LocationFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class MoveCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (this.getPermission().hasPerm(sender, true, false)) {
                if (!(sender instanceof Conversable)) {
                    Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                    return true;
                }
                Hologram h = HoloAPI.getManager().getHologram(args[1]);
                if (h == null) {
                    Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                    return true;
                }
                if (sender instanceof Player) {
                    Location to = ((Player) sender).getLocation();
                    h.move(to);
                    Lang.sendTo(sender, Lang.HOLOGRAM_MOVED.getValue());
                } else {
                    InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
                        Hologram h;

                        @Override
                        public void onFunction(ConversationContext context, String input) {
                            h.move(this.getLocation());
                        }

                        @Override
                        public String getSuccessMessage(ConversationContext context, String input) {
                            return Lang.HOLOGRAM_MOVED.getValue();
                        }
                    })).buildConversation((Conversable) sender).begin();
                }
                return true;
            } else return true;
        } else if (args.length == 6) {
            Hologram h = HoloAPI.getManager().getHologram(args[1]);
            if (h == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                return true;
            }
            Location to = GeneralUtil.readLocation(2, args);
            if (to == null) {
                Lang.sendTo(sender, Lang.NOT_LOCATION.getValue());
                return true;
            }
            h.move(to);
            Lang.sendTo(sender, Lang.HOLOGRAM_MOVED.getValue());
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<id>", this.getPermission(), "Move a hologram to your current position."),
                new CommandHelp(this, "<id> <world> <x> <y> <z>", this.getPermission(), "Move a hologram to a new position.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.move");
    }
}