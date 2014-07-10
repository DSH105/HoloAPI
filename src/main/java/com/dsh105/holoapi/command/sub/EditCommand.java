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
import com.dsh105.commodus.GeneralUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

public class EditCommand implements CommandListener {

    @Command(
            command = "edit <id> <line_number>",
            description = "Edit a line of an existing hologram",
            permission = "holoapi.holo.edit",
            help = "You will be prompted for the content after the command is entered"
    )
    public boolean edit(CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
        }

        final Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        int lineNumber;
        try {
            lineNumber = GeneralUtil.toInteger(event.variable("line_number"));
        } catch (NumberFormatException e) {
            event.respond(Lang.INT_ONLY.getValue("string", event.variable("line_number")));
            return true;
        }

        if (lineNumber > hologram.getLines().length) {
            event.respond(Lang.LINE_INDEX_TOO_BIG.getValue("index", event.variable("line_number")));
            return true;
        }
        final int index = lineNumber;

        InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new SimpleInputFunction() {

            @Override
            public void onFunction(ConversationContext context, String input) {
                hologram.updateLine(index - 1, input);
            }

            @Override
            public String getSuccessMessage(ConversationContext context, String input) {
                return Lang.HOLOGRAM_UPDATE_LINE.getValue("index", index, "input", ChatColor.translateAlternateColorCodes('&', input));
            }

            @Override
            public String getPromptText(ConversationContext context) {
                return Lang.PROMPT_UPDATE_LINE.getValue();
            }

            @Override
            public String getFailedText(ConversationContext context, String invalidInput) {
                return "";
            }
        })).buildConversation((Conversable) event.sender()).begin();
        return true;
    }

    @Command(
            command = "edit <id> <line> <content...>",
            description = "Edit a line of an existing hologram",
            permission = "holoapi.holo.edit",
            help = "The content can be more than one word"
    )
    public boolean editWithContent(CommandEvent event) {
        final Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        int lineNumber;
        try {
            lineNumber = GeneralUtil.toInteger(event.variable("line_number"));
        } catch (NumberFormatException e) {
            event.respond(Lang.INT_ONLY.getValue("string", event.variable("line_number")));
            return true;
        }

        if (lineNumber > hologram.getLines().length) {
            event.respond(Lang.LINE_INDEX_TOO_BIG.getValue("index", event.variable("line_number")));
            return true;
        }

        hologram.updateLine(lineNumber - 1, event.variable("content"));
        event.respond(Lang.HOLOGRAM_UPDATE_LINE.getValue("index", lineNumber, "input", ChatColor.translateAlternateColorCodes('&', event.variable("content"))));
        return true;
    }
}