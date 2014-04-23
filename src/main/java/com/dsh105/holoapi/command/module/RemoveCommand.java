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
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.conversation.basic.YesNoFunction;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

public class RemoveCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            final Hologram h = HoloAPI.getManager().getHologram(args[1]);
            if (h == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                return true;
            }
            final String hologramId = h.getSaveId();

            if (sender instanceof Conversable) {
                InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new YesNoFunction() {

                    private boolean success;

                    @Override
                    public void onFunction(ConversationContext context, String input) {
                        if (input.equalsIgnoreCase("YES")) {
                            HoloAPI.getManager().clearFromFile(hologramId);
                            success = true;
                        }
                    }

                    @Override
                    public String getSuccessMessage(ConversationContext context, String input) {
                        return success ? Lang.HOLOGRAM_CLEARED_FILE.getValue().replace("%id%", hologramId) : Lang.HOLOGRAM_REMOVED_MEMORY.getValue().replace("%id%", hologramId);
                    }

                    @Override
                    public String getPromptText(ConversationContext context) {
                        return Lang.YES_NO_CLEAR_FROM_FILE.getValue();
                    }

                    @Override
                    public String getFailedText(ConversationContext context, String invalidInput) {
                        return Lang.YES_NO_INPUT_INVALID.getValue();
                    }
                })).buildConversation((Conversable) sender).begin();
            } else {
                HoloAPI.getManager().clearFromFile(hologramId);
                Lang.sendTo(sender, Lang.HOLOGRAM_CLEARED_FILE.getValue().replace("%id%", hologramId));
            }

            HoloAPI.getManager().stopTracking(h);
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<id>", this.getPermission(), "Remove an existing holographic display using its ID.")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.remove");
    }
}