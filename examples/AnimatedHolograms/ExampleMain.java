package com.dsh105.holoapi.example;

import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.AnimatedHologramFactory;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example:
 * - This example adds one command - /animation
 * - This command creates an animated hologram consisting of three text frames
 * <p/>
 * What you will achieve by following this:
 * - Knowledge of how to create an animated hologram using frames of text
 */
public class ExampleMain extends JavaPlugin {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't do that!");
            return true;
        }
        if (command.getName().equalsIgnoreCase("animation")) {
            // Create our animated text hologram
            // It will consist of three frames, each varying in line number. HoloAPI will automatically piece it together for you
            //Animations can have as many frames as you want
            AnimatedHologram h = new AnimatedHologramFactory(this)
                    .withText(new AnimatedTextGenerator(
                            new Frame(5, "First line, first frame", "Many lines", "Such wow"), // The first frame of the animation
                            new Frame(10, "This frame is slightly longer", "And can have a different number of lines than the first"), // The second frame
                            new Frame(15, "&sWow, such colours"))) // In this frame, HoloAPI's &s code is used to create a colourful display of text
                    .build();

            // As well as the methods that the Hologram class provides a few extra methods, such as: getNext(), getCurrent(), getFrames(), getFrame(int)
            // That's pretty much all there is to it
            return true;
        }
        return false;
    }
}