package com.dsh105.holoapi.command;

import com.dsh105.dshutils.pagination.Paginator;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class HoloCommand implements CommandExecutor {

    public String label;
    private Paginator help;

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
        Lang.sendTo(sender, Lang.COMMAND_ERROR.toString()
                .replace("%cmd%", "/" + cmd.getLabel() + " " + (args.length == 0 ? "" : StringUtil.combineSplit(0, args, " "))));
        return true;
    }
}