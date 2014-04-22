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
import java.util.HashMap;
import java.util.Map;

public class CommandModuleManager implements CommandExecutor {

    private HashMap<String, CommandModule> modules = new HashMap<String, CommandModule>();

    private Paginator helpPages;
    private FancyPaginator fancyHelpPages;

    public void registerDefaults() {
        this.register("help", new HelpCommand());
    }

    public void register(String commandArguments, CommandModule module) {
        this.modules.put(commandArguments, module);
    }

    public CommandModule getModuleFor(String commandArguments) {
        return this.modules.get(commandArguments);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            Lang.sendTo(sender, Lang.PLUGIN_INFORMATION.getValue().replace("%version%", HoloAPI.getCore().getDescription().getVersion()));
            return true;
        }

        String moduleArgs = args[0];

        CommandModule module = this.getModuleFor(moduleArgs);

        if (module != null) {
            return module.onCommand(sender, args);
        }

        Lang.sendTo(sender, Lang.COMMAND_DOESNOT_EXIST.getValue().replace("%cmd%", "/" + cmd.getLabel() + (args.length == 0 ? "" : " " + StringUtil.combineSplit(0, args, " "))));
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
                        Object raw = commandHelp.getHelpFor(sender);
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

            for (FancyMessage message : fancyHelpPages.getPage(page)) {
                message.send((Player) sender);
            }
        } else {
            if (helpPages == null) {
                ArrayList<String> list = new ArrayList<String>();
                for (String key : modules.keySet()) {
                    for (CommandHelp commandHelp : modules.get(key).getHelp()) {
                        Object raw = commandHelp.getHelpFor(sender);
                        if (raw instanceof FancyMessage) {
                            continue;
                        } else if (raw instanceof String[]) {
                            for (String part : (String[]) raw) {
                                list.add(part);
                            }
                        }
                    }
                }
                helpPages = new Paginator(list, 6);
            }

            if (helpPages.getPage(page) == null) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Help page " + ChatColor.AQUA + page + ChatColor.DARK_AQUA + " is invalid.");
                return;
            }

            for (String message : helpPages.getPage(page)) {
                sender.sendMessage(message);
            }
        }
    }

    public void sendHelpTo(CommandSender sender, String command) {
        ArrayList<String> suggestions = new ArrayList<String>();
        for (Map.Entry<String, CommandModule> entry : modules.entrySet()) {
            if (command.equalsIgnoreCase(entry.getKey())) {

                return;
            } else if (entry.getKey().toLowerCase().startsWith(command.toLowerCase())) {
                suggestions.add(entry.getKey());
            }
        }

        String suggest = StringUtil.join(suggestions, ", ");
        sender.sendMessage(ChatColor.DARK_AQUA + "Help not found for: " + ChatColor.AQUA + "/holo " + command);
        sender.sendMessage(ChatColor.DARK_AQUA + "Did you mean: " + ChatColor.AQUA + "" + ChatColor.ITALIC + suggest);
    }
}