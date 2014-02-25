package com.dsh105.holoapi.command;

import com.dsh105.dshutils.pagination.Paginator;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class HoloCommand implements CommandExecutor {

    public String label;
    private Paginator help;

    private String[] HELP_CREATE;

    public HoloCommand(String name) {
        this.label = name;

        ArrayList<String> list = new ArrayList<String>();
        for (HelpEntry he : HelpEntry.values()) {
            list.add(he.getLine());
        }
        this.help = new Paginator(list, 5);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        /**
         * Possible commands:
         *
         * ----------------------
         * NOTE: ALL COMMAND CREATED HOLOGRAMS ARE VISIBLE TO ALL PLAYERS
         * ----------------------
         *
         * /holo create [-i <image></image>] [-t <text>] -> Creates hologram. Each switch can be followed by multiple lines, until a new switch is specified. (-i loads an image from config.yml images section)
         * /holo create -> Start conversation for multiple lines -> "Done" when finished
         * /holo info -> Print info on all active holograms (with ids)
         * /holo remove <id> -> remove hologram of id
         */

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")) {
            if (args.length == 1) {
                String[] help = this.help.getPage(1);
                sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help 1/" + this.help.getIndex() + "  " + ChatColor.DARK_AQUA + "----------------");
                sender.sendMessage(ChatColor.DARK_AQUA + "Parameters: <> = Required      [] = Optional");
                for (String s : help) {
                    sender.sendMessage(s);
                }
                sender.sendMessage(ChatColor.DARK_AQUA + "--------------------------------------------------");
                return true;
            } else if (args.length == 2) {
                if (StringUtil.isInt(args[1])) {
                    String[] help = this.help.getPage(Integer.parseInt(args[1]));
                    if (help == null) {
                        Lang.sendTo(sender, Lang.HELP_INDEX_TOO_BIG.toString().replace("%index%", args[1]));
                        return true;
                    }
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + args[1] + "/" + this.help.getIndex() + "  " + ChatColor.DARK_AQUA + "----------------");
                    for (String s : help) {
                        sender.sendMessage(s);
                    }
                    sender.sendMessage(ChatColor.DARK_AQUA + "--------------------------------------------------");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.DARK_AQUA + "----------------" + ChatColor.AQUA + " HoloAPI Help " + ChatColor.DARK_AQUA + "----------------");
                    if (args[1].equalsIgnoreCase("create")) {
                        for (String s : HELP_CREATE) {
                            sender.sendMessage(s);
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_AQUA + "Help could not be found for \"" + ChatColor.AQUA + args[1] + ChatColor.DARK_AQUA + "\".");
                    }
                    sender.sendMessage(ChatColor.DARK_AQUA + "--------------------------------------------------");
                    return true;
                }
            }
            return true;
        } else if (args.length == 1) {

        }
        Lang.sendTo(sender, Lang.COMMAND_ERROR.getValue()
                .replace("%cmd%", "/" + cmd.getLabel() + " " + (args.length == 0 ? "" : StringUtil.combineSplit(0, args, " "))));
        return true;
    }
}