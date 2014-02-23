package com.dsh105.holoapi.command;

import com.dsh105.dshutils.pagination.Paginator;
import com.dsh105.dshutils.util.StringUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.image.ImageChar;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
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
            URI uri = URI.create("http://www.gstatic.com/webp/gallery3/2.png");
            BufferedImage image;
            try {
                image = ImageIO.read(uri.toURL());
            } catch (IOException e) {
                throw new RuntimeException("Cannot read image " + uri, e);
            }
            if (image != null) {
                /*final Hologram h = new HologramFactory().withCoords(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ())//withText("Hi, how's it going?", "Morning", ChatColor.BLUE + "" + ImageChar.MEDIUM_SHADE.getImageChar() + ImageChar.BLOCK.getImageChar() + ' ').build();
                        .withImage(new ImageData("creepermap.png", 8, ImageChar.MEDIUM_SHADE)).build();*/
                final Hologram h = new HologramFactory().withLocation(new Vector(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()), p.getWorld().getName()).withText(new ImageGenerator(image, 25, ImageChar.BLOCK).getLines()).build();
                h.show(p);
                p.sendMessage(Lang.PREFIX.getValue() + "Spawned");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        h.clear(p);
                        p.sendMessage(Lang.PREFIX.getValue() + "Despawned");
                    }
                }.runTaskLater(HoloAPI.getInstance(), 500L);
                return true;
            }
        }
        Lang.sendTo(sender, Lang.COMMAND_ERROR.getValue()
                .replace("%cmd%", "/" + cmd.getLabel() + " " + (args.length == 0 ? "" : StringUtil.combineSplit(0, args, " "))));
        return true;
    }
}