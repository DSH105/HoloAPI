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
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.LocationFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.util.MiscUtil;
import org.bukkit.Location;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReadTxtCommand implements CommandListener {

    @Command(
            command = "readtxt <url>",
            description = "Creates a hologram from a given URL",
            permission = "holoapi.holo.readtxt"
    )
    public boolean command(final CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
        }

        if (event.sender() instanceof Player) {
            event.respond(Lang.READING_TXT.getValue("url", event.variable("url")));
            this.connect(event, event.variable("url"), ((Player) event.sender()).getLocation());
        } else {
            InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
                @Override
                public void onFunction(ConversationContext context, String input) {
                    connect(event, event.variable("url"), this.getLocation());
                }

                @Override
                public String getSuccessMessage(ConversationContext context, String input) {
                    return Lang.READING_TXT.getValue("url", event.variable("url"));
                }
            })).buildConversation((Conversable) event.sender()).begin();
        }
        return true;
    }

    @Command(
            command = "readtxt <url> <world> <x> <y> <z>",
            description = "Creates a hologram from a given URL",
            permission = "holoapi.holo.readtxt"
    )
    public boolean withLocation(CommandEvent event) {
        Location location = MiscUtil.getLocation(event);
        if (location == null) {
            return true;
        }
        event.respond(Lang.READING_TXT.getValue("url", event.variable("url")));
        this.connect(event, event.variable("url"), location);
        return true;
    }

    private void connect(final CommandEvent event, final String url, final Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final String[] lines = MiscUtil.readWebsiteContentsSoWeCanUseTheText(url);
                if (lines != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Hologram hologram = new HologramFactory(HoloAPI.getCore()).withLocation(location).withText(lines).build();
                            event.respond(Lang.HOLOGRAM_CREATED.getValue("id", hologram.getSaveId()));
                        }
                    }.runTask(HoloAPI.getCore());
                }
            }
        }.runTaskAsynchronously(HoloAPI.getCore());
    }
}