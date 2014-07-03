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
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

public class AddLineCommand implements CommandListener {

    @Command(
            command = "addline <id> <line...>",
            description = "Add a new line to an existing hologram",
            permission = "holoapi.holo.addline",
            help = "New line content can be more than one word."
    )
    public boolean withLine(CommandEvent event) {
        Hologram hologram = getHologram(event);
        if (hologram == null) {
            return true;
        }

        HoloAPI.getManager().copyAndAddLineTo(hologram, event.variable("line"));
        event.respond(Lang.HOLOGRAM_ADDED_LINE.getValue("id", hologram.getSaveId()));
        return true;
    }

    @Command(
            command = "addline <id>",
            description = "Add a new line to an existing hologram",
            permission = "holoapi.holo.addline",
            help = {"New line content can be more than one word.", "After entering this command, you will be prompted to enter the new line."}
    )
    public boolean withoutLine(final CommandEvent event) {
        final Hologram hologram = getHologram(event);
        if (hologram == null) {
            return true;
        }
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
            return true;
        }
        InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new SimpleInputFunction() {
            @Override
            public void onFunction(ConversationContext context, String input) {
                HoloAPI.getManager().copyAndAddLineTo(hologram, input);
                event.respond(Lang.HOLOGRAM_ADDED_LINE.getValue("id", hologram.getSaveId()));
            }

            @Override
            public String getSuccessMessage(ConversationContext context, String input) {
                return Lang.HOLOGRAM_ADDED_LINE.getValue("id", input);
            }

            @Override
            public String getPromptText(ConversationContext context) {
                return Lang.PROMPT_ENTER_NEW_LINE.getValue();
            }

            @Override
            public String getFailedText(ConversationContext context, String invalidInput) {
                return null;
            }
        })).buildConversation((Conversable) event.sender()).begin();
        return true;
    }

    private Hologram getHologram(CommandEvent event) {
        final Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return null;
        }

        if (hologram instanceof AnimatedHologram) {
            event.respond(Lang.HOLOGRAM_ADD_LINE_ANIMATED.getValue());
            return null;
        }
        return hologram;
    }
}