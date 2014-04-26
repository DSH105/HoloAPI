package com.dsh105.holoapi.example;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example:
 * - When a player dies, create a hologram at their death location
 * - This hologram will be removed after 30 seconds
 * <p/>
 * What you will achieve by following this:
 * - Knowledge of how to create a simple hologram with no persistence
 * - Knowledge of how to remove a hologram
 */
public class ExampleMain extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player who = event.getEntity();
        // Initiate the factory to build our new hologram
        final Hologram hologram = new HologramFactory(this)
                // This is where we want the hologram to be
                .withLocation(who.getLocation())
                // This hologram will have two lines of text
                .withText("RIP " + who.getName(), "Better luck next time!")
                // We don't want this hologram to save to file, so we can set it to a simple hologram
                .withSimplicity(true)
                // Build the hologram. Also shows to all nearby players
                .build();

        this.getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                // Remove the hologram that was created above
                HoloAPI.getManager().stopTracking(hologram);
            }
        }, 20 * 30);
    }

}