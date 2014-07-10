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
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.builder.BuilderInputPrompt;
import org.bukkit.conversations.Conversable;

public class BuildCommand implements CommandListener {

    @Command(
            command = "build",
            description = "Dynamically build a combined hologram of both text and images",
            permission = "holoapi.holo.build"
    )
    public boolean command(CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
            return true;
        }
        InputFactory.buildBasicConversation().withFirstPrompt(new BuilderInputPrompt()).buildConversation((Conversable) event.sender()).begin();
        return true;
    }
}