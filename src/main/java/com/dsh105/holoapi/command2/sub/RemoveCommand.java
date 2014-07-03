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
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.conversation.basic.YesNoFunction;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

public class RemoveCommand implements CommandListener {

    @Command(
            command = "remove <id>",
            description = "Remove an existing hologram using its ID",
            permission = "holoapi.holo.remove"
    )
    public boolean command(CommandEvent event) {
        final Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        if (event.sender() instanceof Conversable) {
            InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new YesNoFunction() {

                private boolean success;

                @Override
                public void onFunction(ConversationContext context, String input) {
                    if (BooleanUtils.toBoolean(input)) {
                        HoloAPI.getManager().clearFromFile(hologram.getSaveId());
                        success = true;
                    }
                }

                @Override
                public String getSuccessMessage(ConversationContext context, String input) {
                    return success ? Lang.HOLOGRAM_CLEARED_FILE.getValue("id", hologram.getSaveId()) : Lang.HOLOGRAM_REMOVED_MEMORY.getValue("id", hologram.getSaveId());
                }

                @Override
                public String getPromptText(ConversationContext context) {
                    return Lang.YES_NO_CLEAR_FROM_FILE.getValue();
                }

                @Override
                public String getFailedText(ConversationContext context, String invalidInput) {
                    return Lang.YES_NO_INPUT_INVALID.getValue();
                }
            })).buildConversation((Conversable) event.sender()).begin();
        } else {
            HoloAPI.getManager().clearFromFile(hologram.getSaveId());
            event.respond(Lang.HOLOGRAM_CLEARED_FILE.getValue("id", hologram.getSaveId()));
        }

        HoloAPI.getManager().stopTracking(hologram);
        return true;
    }

    @Command(
            command = "remove <id> <keep>",
            description = "Remove an existing hologram using its ID",
            permission = "holoapi.holo.remove",
            help = {"<keep> defines whether the hologram is removed from the save files", "If set to false, the hologram will only be removed from memory"}
    )
    public boolean withKeep(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        if (BooleanUtils.toBoolean(event.variable("keep"))) {
            HoloAPI.getManager().clearFromFile(hologram.getSaveId());
        } else {
            Lang.HOLOGRAM_REMOVED_MEMORY.getValue("id", hologram.getSaveId());
        }

        HoloAPI.getManager().stopTracking(hologram);
        return true;
    }
}