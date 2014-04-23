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
import com.dsh105.holoapi.api.HologramFactory;
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
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReadTxtCommand extends CommandModule {

    @Override
    public boolean onCommand(final CommandSender sender, String[] args) {
        if (args.length == 2) {
            final String url = args[1];
            if (sender instanceof Player) {
                this.connect(sender, url, ((Player) sender).getLocation());
                Lang.sendTo(sender, Lang.READING_TXT.getValue().replace("%url%", url));
            } else {
                if (!(sender instanceof Conversable)) {
                    Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                    return true;
                }
                InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new LocationFunction() {
                    @Override
                    public void onFunction(ConversationContext context, String input) {
                        connect(sender, url, this.getLocation());
                    }

                    @Override
                    public String getSuccessMessage(ConversationContext context, String input) {
                        return Lang.READING_TXT.getValue().replace("%url%", url);
                    }
                }));
            }
            return true;
        }
        return false;
    }

    private void connect(final CommandSender sender, final String url, final Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                final String[] lines = MiscUtil.readWebsiteContentsSoWeCanUseTheText(url);
                if (lines != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Hologram h = new HologramFactory(HoloAPI.getCore()).withLocation(location).withText(lines).build();
                            Lang.sendTo(sender, Lang.HOLOGRAM_CREATED.getValue().replace("%id%", h.getSaveId()));
                        }
                    }.runTask(HoloAPI.getCore());
                }
            }
        }.runTaskAsynchronously(HoloAPI.getCore());
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<url>", this.getPermission(), "Creates a hologram from a given URL.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.readtxt");
    }
}