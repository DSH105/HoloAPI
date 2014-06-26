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

import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

public class AddLineCommand extends CommandModule {

    @Override
    public boolean onCommand(final CommandSender sender, String[] args) {
        if (args.length >= 2) {
            final Hologram hologram = HoloAPI.getManager().getHologram(args[1]);
            if (hologram == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                return true;
            }

            if (hologram instanceof AnimatedHologram) {
                Lang.sendTo(sender, Lang.HOLOGRAM_ADD_LINE_ANIMATED.getValue());
                return true;
            }

            if (!(sender instanceof Conversable)) {
                Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                return true;
            }

            if (args.length >= 3) {
                HoloAPI.getManager().copyAndAddLineTo(hologram, StringUtil.combineSplit(2, args, " "));
                Lang.sendTo(sender, Lang.HOLOGRAM_ADDED_LINE.getValue().replace("%id%", args[1]));
            } else {
                InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new SimpleInputFunction() {
                    @Override
                    public void onFunction(ConversationContext context, String input) {
                        HoloAPI.getManager().copyAndAddLineTo(hologram, input);
                        Lang.sendTo(sender, Lang.HOLOGRAM_ADDED_LINE.getValue().replace("%id%", input));
                    }

                    @Override
                    public String getSuccessMessage(ConversationContext context, String input) {
                        return Lang.HOLOGRAM_ADDED_LINE.getValue().replace("%id%", input);
                    }

                    @Override
                    public String getPromptText(ConversationContext context) {
                        return Lang.PROMPT_ENTER_NEW_LINE.getValue();
                    }

                    @Override
                    public String getFailedText(ConversationContext context, String invalidInput) {
                        return null;
                    }
                }));
            }
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<id> <line>", this.getPermission(), "Add a new line to an existing hologram.", "New line content can be more than one word."),
                new CommandHelp(this, "<id>", this.getPermission(), "Add a new line to an existing hologram.", "New line content can be more than one word.", "After entering this command, you will be prompted to enter the new line.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.addline");
    }
}