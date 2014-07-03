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

package com.dsh105.holoapi.command2.sub;

import com.dsh105.command.Command;
import com.dsh105.command.CommandEvent;
import com.dsh105.command.CommandListener;
import com.dsh105.commodus.GeneralUtil;
import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.LocationFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;

public class CopyCommand implements CommandListener {

    @Command(
            command = "copy <id>",
            description = "Copy a hologram to your current position",
            permission = "holoapi.holo.copy"
    )
    public boolean command(CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
        }

        final Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        if (event.sender() instanceof Player) {
            Hologram copy = HoloAPI.getManager().copy(hologram, ((Player) event.sender()).getLocation());
            if (copy instanceof AnimatedHologram) {
                event.respond(Lang.HOLOGRAM_ANIMATED_COPIED.getValue("id", copy.getSaveId()));
            } else {
                event.respond(Lang.HOLOGRAM_COPIED.getValue("id", copy.getSaveId()));
            }
            return true;
        }
        InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
            Hologram copy;
            @Override
            public void onFunction(ConversationContext context, String input) {
                copy = HoloAPI.getManager().copy(hologram, getLocation());
            }

            @Override
            public String getSuccessMessage(ConversationContext context, String input) {
                if (copy instanceof AnimatedHologram) {
                    return Lang.HOLOGRAM_ANIMATED_COPIED.getValue("id", copy.getSaveId());
                }
                return Lang.HOLOGRAM_COPIED.getValue("id", copy.getSaveId());
            }
        })).buildConversation((Conversable) event.sender()).begin();
        return true;
    }

    @Command(
            command = "copy <id> <world> <x> <y> <z>",
            description = "Copy a hologram to a new position",
            permission = "holoapi.holo.copy"
    )
    public boolean withLocation(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        Location location = MiscUtil.getLocation(event);
        if (location == null) {
            return true;
        }
        Hologram copy = HoloAPI.getManager().copy(hologram, location);
        if (copy instanceof AnimatedHologram) {
            event.respond(Lang.HOLOGRAM_ANIMATED_COPIED.getValue("id", copy.getSaveId()));
        } else {
            event.respond(Lang.HOLOGRAM_COPIED.getValue("id", copy.getSaveId()));
        }
        return true;
    }
}