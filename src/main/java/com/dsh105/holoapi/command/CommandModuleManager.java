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

package com.dsh105.holoapi.command;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.command.module.*;
import com.dsh105.holoapi.util.Lang;
import com.dsh105.holoapi.util.StringUtil;
import com.dsh105.holoapi.util.fanciful.FancyMessage;
import com.dsh105.holoapi.util.pagination.FancyPaginator;
import com.dsh105.holoapi.util.pagination.Paginator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommandModuleManager implements CommandExecutor {

    private LinkedHashMap<String, CommandModule> modules = new LinkedHashMap<String, CommandModule>();

    private Paginator helpPages;
    private FancyPaginator fancyHelpPages;

    public void registerDefaults() {
        this.register("help", new HelpCommand());
        this.register("create", new CreateCommand());
        this.register("remove", new RemoveCommand());
        this.register("addline", new AddLineCommand());
        this.register("edit", new EditCommand());
        this.register("info", new InfoCommand());
        this.register("nearby", new NearbyCommand());
        this.register("move", new MoveCommand());
        this.register("teleport", new TeleportCommand());
        this.register("copy", new CopyCommand());
        this.register("touch", new TouchCommand());
        this.register("show", new ShowCommand());
        this.register("hide", new HideCommand());
        this.register("visibility", new VisibilityCommand());
        this.register("build", new BuildCommand());
        this.register("readtxt", new ReadTxtCommand());
        this.register("refresh", new RefreshCommand());
        this.register("reload", new ReloadCommand());
        this.register("update", new UpdateCommand());
    }

    public void register(String subCommand, CommandModule module) {
        this.modules.put(subCommand, module);
        module.setSubCommand(subCommand);
    }

    public CommandModule getModuleFor(String commandArguments) {
        return this.modules.get(commandArguments);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length <= 0) {
            Lang.sendTo(sender, Lang.PLUGIN_INFORMATION.getValue().replace("%version%", HoloAPI.getCore().getDescription().getVersion()));
            return true;
        }

        String moduleArgs = args[0];

        CommandModule module = this.getModuleFor(moduleArgs);

        if (module != null) {
            // Reject if they don't have permission
            if (module.getPermission() != null && !module.getPermission().hasPerm(sender, true, true)) {
                return true;
            }

            // Perform under the correct module
            if (!module.onCommand(sender, args)) {
                if (args.length == 1) {
                    this.sendHelpTo(sender, moduleArgs);
                } else {
                    sender.sendMessage(ChatColor.DARK_AQUA + "Sub command not found for: " + ChatColor.AQUA + "/holo " + moduleArgs);
                }
            }
            return true;
        }

        Lang.sendTo(sender, Lang.COMMAND_DOESNOT_EXIST.getValue().replace("%cmd%", "/" + cmd.getLabel() + (args.length == 0 ? "" : " " + StringUtil.combineSplit(0, args, " "))));

        ArrayList<String> suggestions = new ArrayList<String>();
        for (Map.Entry<String, CommandModule> entry : modules.entrySet()) {
            if (entry.getKey().toLowerCase().startsWith(moduleArgs.toLowerCase())) {
                suggestions.add(entry.getKey());
            }
        }
        if (!suggestions.isEmpty()) {
            String suggest = StringUtil.join(suggestions, ", ");
            sender.sendMessage(ChatColor.DARK_AQUA + "Did you mean: " + ChatColor.AQUA + "" + ChatColor.ITALIC + suggest);
        }
        return true;
    }

    public void sendHelpTo(CommandSender sender) {
        this.sendHelpTo(sender, 1);
    }

    public void sendHelpTo(CommandSender sender, int page) {
        if (HoloAPI.getCore().isUsingNetty && sender instanceof Player) {
            if (fancyHelpPages == null) {
                ArrayList<FancyMessage> list = new ArrayList<FancyMessage>();
                for (String key : modules.keySet()) {
                    for (CommandHelp commandHelp : modules.get(key).getHelp()) {
                        Object raw = commandHelp.getHelpFor(sender, true);
                        if (!(raw instanceof FancyMessage)) {
                            continue;
                        }
                        list.add((FancyMessage) raw);
                    }
                }
                fancyHelpPages = new FancyPaginator(list, 6);
            }

            if (fancyHelpPages.getPage(page) == null) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Help page " + ChatColor.AQUA + page + ChatColor.DARK_AQUA + " is invalid.");
                return;
            }

            sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + page + "/" + this.fancyHelpPages.getIndex() + " " + ChatColor.DARK_AQUA + "----------------");
            for (FancyMessage message : fancyHelpPages.getPage(page)) {
                message.send((Player) sender);
            }
        } else {
            if (helpPages == null) {
                ArrayList<String> list = new ArrayList<String>();
                for (String key : modules.keySet()) {
                    for (CommandHelp commandHelp : modules.get(key).getHelp()) {
                        Object raw = commandHelp.getHelpFor(sender, true);
                        if (raw instanceof FancyMessage) {
                            continue;
                        } else if (raw instanceof String[]) {
                            for (String part : (String[]) raw) {
                                list.add(part);
                            }
                        } else if (raw instanceof String) {
                            list.add((String) raw);
                        }
                    }
                }
                helpPages = new Paginator(list, 6);
            }

            if (helpPages.getPage(page) == null) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Help page " + ChatColor.AQUA + page + ChatColor.DARK_AQUA + " is invalid.");
                return;
            }

            sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + page + "/" + this.helpPages.getIndex() + " " + ChatColor.DARK_AQUA + "----------------");
            for (String message : helpPages.getPage(page)) {
                sender.sendMessage(message);
            }
        }
    }

    public void sendHelpTo(CommandSender sender, String command) {
        this.sendHelpTo(sender, command, false);
    }

    public void sendHelpTo(CommandSender sender, String command, boolean shorten) {
        ArrayList<String> suggestions = new ArrayList<String>();
        for (Map.Entry<String, CommandModule> entry : modules.entrySet()) {
            if (command.equalsIgnoreCase(entry.getKey())) {
                sender.sendMessage(ChatColor.DARK_AQUA + "-------------" + ChatColor.AQUA + " HoloAPI Help Suggestions " + ChatColor.DARK_AQUA + "-------------");
                for (CommandHelp help : entry.getValue().getHelp()) {
                    help.sendHelpTo(sender, shorten);
                }
                return;
            } else if (entry.getKey().toLowerCase().startsWith(command.toLowerCase())) {
                suggestions.add(entry.getKey());
            }
        }

        sender.sendMessage(ChatColor.DARK_AQUA + "Help not found for: " + ChatColor.AQUA + "/holo " + command);
        if (!suggestions.isEmpty()) {
            String suggest = StringUtil.join(suggestions, ", ");
            sender.sendMessage(ChatColor.DARK_AQUA + "Did you mean: " + ChatColor.AQUA + "" + ChatColor.ITALIC + suggest);
        }
    }
}