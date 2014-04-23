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

import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.builder.BuilderInputPrompt;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;

public class BuildCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Conversable)) {
                Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                return true;
            }
            InputFactory.buildBasicConversation().withFirstPrompt(new BuilderInputPrompt()).buildConversation((Conversable) sender).begin(); //TODO
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "Dynamically build a combined hologram of both text and images.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.build");
    }
}