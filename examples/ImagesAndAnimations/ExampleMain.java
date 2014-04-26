package com.dsh105.holoapi.example;

import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.AnimatedHologramFactory;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.ImageChar;
import com.dsh105.holoapi.image.ImageGenerator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Example:
 * - This example adds two commands. /image and /animation
 * - /image will create an image hologram
 * - /animation will create two animated holograms
 * <p/>
 * What you will achieve by following this:
 * - Knowledge of how to create image holograms using both saved files and URLs
 * - Knowledge of how to create animated holograms from a GIF file
 * - Knowledge of how to create animated holograms using ImageGenerators as separate frames
 * - Knowledge of how to create a simple hologram with no persistence
 * - Knowledge of how to remove a hologram
 */
public class ExampleMain extends JavaPlugin {

    @Override
    public void onEnable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't do that!");
            return true;
        }
        if (command.getName().equalsIgnoreCase("image")) {
            // Here a new image hologram is created from the given URL (which happens to be the HoloAPI logo)
            // The height has been set to 10, the char type to BLOCK and border to false
            // Because of the nature of this image, it's not going to look the best in-game
            // Selecting images that have clear colour outlines (and possibly even pixel-art) will work the best
            Hologram h = new HologramFactory(this)
                    .withLocation(((Player) sender).getLocation())
                    .withImage(new ImageGenerator("http://dev.bukkit.org/media/images/70/44/Banner_PNG.png",
                            10, // A height of 10
                            ImageChar.BLOCK, // Char type to use
                            false)).build(); // No extra border for this image, seeing as it is a filled image with not much transparency
            sender.sendMessage("You created an image hologram at " + h.getDefaultX() + ", " + h.getDefaultY() + ", " + h.getDefaultZ() + "!");
            return true;
        } else if (command.getName().equalsIgnoreCase("animation")) {
            // Animated holograms from GIFs are slightly different
            // If you haven't seen the tutorial on text animations, it is recommended you check that out first
            // We'll go  over two ways of creating images here

            // In this example, we're pretending there's a GIF file located at `plugins/MyPlugin/mygif.gif`
            AnimatedHologram h = new AnimatedHologramFactory(this)
                    .withLocation(((Player) sender).getLocation())
                    .withImage(new AnimatedImageGenerator(new File(this.getDataFolder() + File.separator + "mygif.gif"), 5, 10, ImageChar.BLOCK, false))
                    .build();

            // This will overlap the one above. For the purpose of this tutorial, that doesn't matter
            // The ImageGenerator above can be used to create the frames of AnimatedHolograms
            // This hologram has three different frames created from different PNG images
            // From what we did above, you should be able to figure out what's going on here :)
            AnimatedHologram h2 = new AnimatedHologramFactory(this)
                    .withLocation(((Player) sender).getLocation())
                    .withImage(new AnimatedImageGenerator(5,
                            new ImageGenerator(new File(this.getDataFolder() + File.separator + "firstframe.png"), 10, ImageChar.BLOCK, false),
                            new ImageGenerator(new File(this.getDataFolder() + File.separator + "secondframe.png"), 10, ImageChar.BLOCK, false),
                            new ImageGenerator(new File(this.getDataFolder() + File.separator + "thirdframe.png"), 10, ImageChar.BLOCK, false)))
                    .build();

            sender.sendMessage("You created two animated holograms! Well done!");
            return true;
        }
        return false;
    }
}