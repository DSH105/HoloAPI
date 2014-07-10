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
import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.touch.CommandTouchAction;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.config.Lang;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.conversation.basic.YesNoFunction;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

public class TouchCommand implements CommandListener {

    @Command(
            command = "touch add <id> <command...>",
            description = "Add an action to perform when a certain hologram is touched",
            permission = "holoapi.holo.touch.add"
    )
    public boolean add(final CommandEvent event) {
        if (!(event.sender() instanceof Conversable)) {
            event.respond(Lang.NOT_CONVERSABLE.getValue());
        }

        final Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new YesNoFunction() {
            @Override
            public void onFunction(ConversationContext context, String input) {
                hologram.addTouchAction(new CommandTouchAction(event.variable("command"), BooleanUtils.toBoolean(input)));
            }

            @Override
            public String getSuccessMessage(ConversationContext context, String input) {
                return Lang.COMMAND_TOUCH_ACTION_ADDED.getValue().replace("%command%", "/" + event.variable("command")).replace("%id%", hologram.getSaveId());
            }

            @Override
            public String getPromptText(ConversationContext context) {
                return Lang.YES_NO_COMMAND_TOUCH_ACTION_AS_CONSOLE.getValue();
            }

            @Override
            public String getFailedText(ConversationContext context, String invalidInput) {
                return Lang.YES_NO_INPUT_INVALID.getValue();
            }
        })).buildConversation((Conversable) event.sender()).begin();
        return true;
    }

    @Command(
            command = "touch add <id> <r:true|false|yes|no,n:as_console> <command...>",
            description = "Add an action to perform when a certain hologram is touched",
            permission = "holoapi.holo.touch.add"
    )
    public boolean addWithBoolean(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        hologram.addTouchAction(new CommandTouchAction(event.variable("command"), BooleanUtils.toBoolean(event.variable("as_console"))));
        event.respond(Lang.COMMAND_TOUCH_ACTION_ADDED.getValue("command", "/" + event.variable("command"), "id", hologram.getSaveId()));
        return true;
    }

    @Command(
            command = "touch remove <id> <touch_id...>",
            description = "Add an action to perform when a certain hologram is touched",
            permission = "holoapi.holo.touch.remove"
    )
    public boolean remove(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        TouchAction toRemove = null;
        for (TouchAction touchAction : hologram.getAllTouchActions()) {
            if (touchAction != null && touchAction.getSaveKey().equalsIgnoreCase(event.variable("touch_id")) || (touchAction instanceof CommandTouchAction && ((CommandTouchAction) touchAction).getCommand().equalsIgnoreCase(event.variable("touch_id")))) {
                toRemove = touchAction;
                break;
            }
        }
        if (toRemove == null) {
            event.respond(Lang.TOUCH_ACTION_NOT_FOUND.getValue("touchid", event.variable("touch_id")));
            return true;
        }

        hologram.removeTouchAction(toRemove);
        if (toRemove instanceof CommandTouchAction) {
            Lang.COMMAND_TOUCH_ACTION_REMOVED.getValue("command", "/" + event.variable("touch_id"), "id", hologram.getSaveId());
        } else {
            Lang.TOUCH_ACTION_REMOVED.getValue("touchid", event.variable("touch_id"), "id", hologram.getSaveId());
        }
        return true;
    }

    @Command(
            command = "touch clear <id>",
            description = "Add an action to perform when a certain hologram is touched",
            permission = "holoapi.holo.touch.clear"
    )
    public boolean clear(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        if (hologram.getAllTouchActions().isEmpty()) {
            event.respond(Lang.NO_TOUCH_ACTIONS.getValue("id", hologram.getSaveId()));
            return true;
        }

        hologram.clearAllTouchActions();
        event.respond(Lang.TOUCH_ACTIONS_CLEARED.getValue("id", hologram.getSaveId()));
        return true;
    }

    @Command(
            command = "touch info <id>",
            description = "Add an action to perform when a certain hologram is touched",
            permission = "holoapi.holo.touch.info"
    )
    public boolean info(CommandEvent event) {
        Hologram hologram = HoloAPI.getManager().getHologram(event.variable("id"));
        if (hologram == null) {
            event.respond(Lang.HOLOGRAM_NOT_FOUND.getValue("id", event.variable("id")));
            return true;
        }

        if (hologram.getAllTouchActions().isEmpty()) {
            event.respond(Lang.NO_TOUCH_ACTIONS.getValue("id", hologram.getSaveId()));
            return true;
        }

        event.respond(Lang.TOUCH_ACTIONS.getValue("id", hologram.getSaveId()));
        for (TouchAction action : hologram.getAllTouchActions()) {
            if (action instanceof CommandTouchAction) {
                event.respond("•• " + "{c1}Command {c2}/" + ((CommandTouchAction) action).getCommand() + " {c1}" + (((CommandTouchAction) action).shouldPerformAsConsole() ? "as console" : "as player"));
                continue;
            }

            if (action.getSaveKey() == null) {
                event.respond("•• {c1}Unidentified TouchAction");
            } else {
                event.respond("•• {c2}" + StringUtil.capitalise(action.getSaveKey().replace("_", " ")));
            }
        }
        return true;
    }
}