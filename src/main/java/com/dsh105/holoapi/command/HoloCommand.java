package com.dsh105.holoapi.command;

import com.dsh105.dshutils.pagination.Paginator;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.HoloPlugin;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.image.ImageChar;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        if (args.length == 0 && sender instanceof Player) {
            final Player p = (Player) sender;
            final Hologram h = new HologramFactory().withCoords(p.getLocation().getX(), p.getLocation().getX(), p.getLocation().getX()).withText("Hi, how's it going?", "Morning", ChatColor.BLUE + "" + ImageChar.MEDIUM_SHADE + ImageChar.BLOCK + ChatColor.MAGIC + ImageChar.MEDIUM_SHADE).build();
            h.show(p);
            p.sendMessage("Spawned");

            new BukkitRunnable() {
                @Override
                public void run() {
                    h.clear(p);
                    p.sendMessage("Despawned");
                }
            }.runTaskLater(HoloPlugin.getInstance(), 60L);
        }
        Lang.sendTo(sender, Lang.COMMAND_ERROR.getValue()
                .replace("%cmd%", "/" + cmd.getLabel() + " " + (args.length == 0 ? "" : StringUtil.combineSplit(0, args, " "))));
        return true;
    }
}