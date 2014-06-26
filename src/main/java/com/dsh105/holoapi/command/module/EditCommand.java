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

import com.dsh105.commodus.GeneralUtil;
import com.dsh105.commodus.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.command.CommandHelp;
import com.dsh105.holoapi.command.CommandModule;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputFunction;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

public class EditCommand extends CommandModule {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            final Hologram h = HoloAPI.getManager().getHologram(args[1]);
            if (h == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[1]));
                return true;
            }
            if (!GeneralUtil.isInt(args[2])) {
                Lang.sendTo(sender, Lang.INT_ONLY.getValue().replace("%string%", args[2]));
                return true;
            }
            final int index = Integer.parseInt(args[2]);
            if (index > h.getLines().length) {
                Lang.sendTo(sender, Lang.LINE_INDEX_TOO_BIG.getValue().replace("%index%", args[2]));
                return true;
            }

            if (args.length >= 4) {
                String content = StringUtil.combineSplit(3, args, " ");
                h.updateLine(index - 1, content);
                Lang.sendTo(sender, Lang.HOLOGRAM_UPDATE_LINE.getValue().replace("%index%", index + "").replace("%input%", ChatColor.translateAlternateColorCodes('&', content)));
            } else {
                InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new SimpleInputFunction() {

                    @Override
                    public void onFunction(ConversationContext context, String input) {
                        h.updateLine(index - 1, input);
                    }

                    @Override
                    public String getSuccessMessage(ConversationContext context, String input) {
                        return Lang.HOLOGRAM_UPDATE_LINE.getValue().replace("%index%", index + "").replace("%input%", ChatColor.translateAlternateColorCodes('&', input));
                    }

                    @Override
                    public String getPromptText(ConversationContext context) {
                        return Lang.PROMPT_UPDATE_LINE.getValue();
                    }

                    @Override
                    public String getFailedText(ConversationContext context, String invalidInput) {
                        return "";
                    }
                })).buildConversation((Conversable) sender).begin();
            }
            return true;
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "<id> <line>", this.getPermission(), "Edit a line of an existing hologram.", "You will be prompted for the content after the command is entered."),
                new CommandHelp(this, "<id> <line> <content>", this.getPermission(), "Edit a line of an existing hologram.", "The content can be more than one word")
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("holoapi.holo.edit");
    }
}