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
import com.dsh105.holoapi.util.ItemUtil;
import com.dsh105.holoapi.util.Permission;
import com.dsh105.holoapi.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHelp {

    private String commandArguments;
    private Permission permission;
    private String[] description;

    private String command;
    private String[] fullDescription;

    public CommandHelp(CommandModule module, String... description) {
        this(module, module.getPermission(), description);
    }

    public CommandHelp(CommandModule module, Permission permission, String... description) {
        this(module.getSubCommand(), description);
        if (permission != null) {
            this.permission = permission;
        }
    }

    public CommandHelp(CommandModule module, String commandArguments, Permission permission, String... description) {
        this(module.getSubCommand() + " " + commandArguments, description);
        if (permission != null) {
            this.permission = permission;
        }
    }

    private CommandHelp(String commandArguments, String... description) {
        this.commandArguments = commandArguments;
        this.description = description;

        this.command = HoloAPI.getCommandLabel() + " " + this.getCommandArguments();
        fullDescription = new String[this.getDescription().length + 1];
        fullDescription[0] = ChatColor.AQUA + "" + ChatColor.BOLD + "Usage for /" + this.command + ":";

        for (int i = 1; i < fullDescription.length; i++) {
            fullDescription[i] = "• " + ChatColor.DARK_AQUA + description[i - 1];
        }
    }

    public String getCommandArguments() {
        return commandArguments;
    }

    public Permission getPermission() {
        return permission;
    }

    public String[] getDescription() {
        return description;
    }

    public String getCommand() {
        return this.command;
    }

    public Object getHelpFor(CommandSender sender) {
        return this.getHelpFor(sender, false);
    }

    public Object getHelpFor(CommandSender sender, boolean shorten) {
        if (HoloAPI.getCore().isUsingNetty && sender instanceof Player) {
            boolean hasPerm = false;
            boolean suggest = true;
            if (this.getPermission() != null) {
                hasPerm = this.getPermission().hasPerm(sender, false, true);
                suggest = hasPerm;
            }
            FancyMessage msg = new FancyMessage(ChatColor.WHITE + "• " + ChatColor.AQUA + this.getCommand()).itemTooltip(ItemUtil.getItem(this.generateDescription(sender)));
            if (suggest || hasPerm) {
                msg.suggest(this.getCommand());
            }
            return msg;
        } else {
            if (shorten) {
                return ChatColor.AQUA + "/" + this.getCommand() + ChatColor.WHITE + "  •••  " + ChatColor.DARK_AQUA + description[0];
            } else {
                return this.generateDescription(sender);
            }
        }
    }

    public void sendHelpTo(CommandSender sender) {
        this.sendHelpTo(sender, false);
    }

    public void sendHelpTo(CommandSender sender, boolean shorten) {
        if (HoloAPI.getCore().isUsingNetty && sender instanceof Player) {
            ((FancyMessage) this.getHelpFor(sender, shorten)).send((Player) sender);
        } else {
            for (String part : (String[]) getHelpFor(sender, shorten)) {
                sender.sendMessage(part);
            }
        }
    }

    private String[] generateDescription(CommandSender sender) {
        if (sender == null || this.getPermission() == null) {
            return fullDescription;
        }
        String[] str = new String[fullDescription.length + 1];
        for (int i = 0; i < fullDescription.length; i++) {
            str[i] = fullDescription[i];
        }
        str[str.length - 1] = "• " + (this.getPermission().hasPerm(sender, false, true) ? ChatColor.GREEN + "" + ChatColor.ITALIC + "You may use this command" : ChatColor.RED + "" + ChatColor.ITALIC + "You do not have permission to use this command.");
        return str;
    }
}