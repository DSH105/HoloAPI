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
import com.dsh105.holoapi.api.touch.CommandTouchAction;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.conversation.InputFactory;
import com.dsh105.holoapi.conversation.basic.SimpleInputPrompt;
import com.dsh105.holoapi.conversation.basic.YesNoFunction;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.Permission;
import com.dsh105.holoapi.util.StringUtil;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;

import java.util.regex.Pattern;

public class TouchCommand extends CommandModule {

    Permission basePerm = new Permission("holoapi.holo.touch");

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            final Hologram hologram = HoloAPI.getManager().getHologram(args[2]);
            if (hologram == null) {
                Lang.sendTo(sender, Lang.HOLOGRAM_NOT_FOUND.getValue().replace("%id%", args[2]));
                return true;
            }

            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("info")) {
                    if (new Permission(this.basePerm, "info").hasPerm(sender, true, true)) {
                        if (hologram.getAllTouchActions().isEmpty()) {
                            Lang.sendTo(sender, Lang.NO_TOUCH_ACTIONS.getValue().replace("%id%", args[2]));
                            return true;
                        }

                        Lang.sendTo(sender, Lang.TOUCH_ACTIONS.getValue().replace("%id%", args[2]));
                        for (TouchAction touchAction : hologram.getAllTouchActions()) {
                            if (touchAction instanceof CommandTouchAction) {
                                sender.sendMessage("•• " + ChatColor.AQUA + "Command" + ChatColor.DARK_AQUA + " /" + ((CommandTouchAction) touchAction).getCommand() + ChatColor.AQUA + " " + (((CommandTouchAction) touchAction).shouldPerformAsConsole() ? "as console" : "as player"));
                            } else {
                                if (touchAction.getSaveKey() == null) {
                                    sender.sendMessage("•• " + ChatColor.AQUA + "Unidentified TouchAction.");
                                } else {
                                    sender.sendMessage("•• " + ChatColor.AQUA + StringUtil.capitalise(touchAction.getSaveKey().replace("_", " ")));
                                }
                            }
                        }
                        return true;
                    } else return true;
                } else if (args[1].equalsIgnoreCase("clear")) {
                    if (new Permission(this.basePerm, "clear").hasPerm(sender, true, true)) {
                        if (hologram.getAllTouchActions().isEmpty()) {
                            Lang.sendTo(sender, Lang.NO_TOUCH_ACTIONS.getValue().replace("%id%", args[2]));
                            return true;
                        }

                        hologram.clearAllTouchActions();
                        Lang.sendTo(sender, Lang.TOUCH_ACTIONS_CLEARED.getValue().replace("%id%", args[2]));
                        return true;
                    } else return true;
                }
            } else if (args.length >= 4) {
                if (args.length >= 5 && args[1].equalsIgnoreCase("add") && Pattern.compile("\\b(?i)(true|false|yes|no)\\b").matcher(args[3]).matches()) {
                    String command = StringUtil.combineSplit(4, args, " ");
                    hologram.addTouchAction(new CommandTouchAction(command, BooleanUtils.toBoolean(args[3])));
                    Lang.sendTo(sender, Lang.COMMAND_TOUCH_ACTION_ADDED.getValue().replace("%command%", "/" + command).replace("%id%", hologram.getSaveId()));
                    return true;
                } else {
                    final String command = StringUtil.combineSplit(3, args, " ");
                    if (args[1].equalsIgnoreCase("add")) {
                        if (new Permission(this.basePerm, "add").hasPerm(sender, true, true)) {
                            if (!(sender instanceof Conversable)) {
                                Lang.sendTo(sender, Lang.NOT_CONVERSABLE.getValue());
                                return true;
                            }
                            InputFactory.buildBasicConversation().withFirstPrompt(new SimpleInputPrompt(new YesNoFunction() {
                                @Override
                                public void onFunction(ConversationContext context, String input) {
                                    hologram.addTouchAction(new CommandTouchAction(command, BooleanUtils.toBoolean(input)));
                                }

                                @Override
                                public String getSuccessMessage(ConversationContext context, String input) {
                                    return Lang.COMMAND_TOUCH_ACTION_ADDED.getValue().replace("%command%", "/" + command).replace("%id%", hologram.getSaveId());
                                }

                                @Override
                                public String getPromptText(ConversationContext context) {
                                    return Lang.YES_NO_COMMAND_TOUCH_ACTION_AS_CONSOLE.getValue();
                                }

                                @Override
                                public String getFailedText(ConversationContext context, String invalidInput) {
                                    return Lang.YES_NO_INPUT_INVALID.getValue();
                                }
                            })).buildConversation((Conversable) sender).begin();
                            return true;
                        } else return true;
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (new Permission(this.basePerm, "remove").hasPerm(sender, true, true)) {
                            TouchAction toRemove = null;
                            for (TouchAction touchAction : hologram.getAllTouchActions()) {
                                if (touchAction != null && touchAction.getSaveKey().equalsIgnoreCase(command) || (touchAction instanceof CommandTouchAction && ((CommandTouchAction) touchAction).getCommand().equalsIgnoreCase(command))) {
                                    toRemove = touchAction;
                                    break;
                                }
                            }
                            if (toRemove == null) {
                                Lang.sendTo(sender, Lang.TOUCH_ACTION_NOT_FOUND.getValue().replace("%touchid%", command));
                                return true;
                            }
                            hologram.removeTouchAction(toRemove);
                            if (toRemove instanceof CommandTouchAction) {
                                Lang.sendTo(sender, Lang.COMMAND_TOUCH_ACTION_REMOVED.getValue().replace("%command%", "/" + command).replace("%id%", hologram.getSaveId()));
                            } else {
                                Lang.sendTo(sender, Lang.TOUCH_ACTION_REMOVED.getValue().replace("%touchid%", command).replace("%id%", hologram.getSaveId()));
                            }
                            return true;
                        } else return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public CommandHelp[] getHelp() {
        return new CommandHelp[]{
                new CommandHelp(this, "add <id> <command>", new Permission(this.basePerm, "add"), "Add an action for a certain hologram to perform when touched.", "Actions defined without the use of the API may only be commands", "Touch Actions are fired when a user left or right clicks a hologram", "The %name% placeholder can be used to define the user that touched the hologram", "Commands can be more than one word."),
                new CommandHelp(this, "add <id> <as_console> <command>", new Permission(this.basePerm, "add"), "Add an action for a certain hologram to perform when touched.", "Actions defined without the use of the API may only be commands", "Touch Actions are fired when a user left or right clicks a hologram", "The %name% placeholder can be used to define the user that touched the hologram", "Commands can be more than one word.", "<as_console> defines whether the action is performed by the console or the player that touched the hologram"),
                new CommandHelp(this, "remove <id> <touch_id>", new Permission(this.basePerm, "remove"), "Remove an action for a TouchScreen hologram", "<touch_id> is the ID of the TouchAction. To remove a command-based Touch Action, simply enter the command"),
                new CommandHelp(this, "clear <id>", new Permission(this.basePerm, "clear"), "Clear all Touch Actions for a particular TouchScreen hologram"),
                new CommandHelp(this, "info <id>", new Permission(this.basePerm, "info"), "View information on all Touch Actions for a particular TouchScreen hologram")
        };
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}