package com.dsh105.holoapi.example;

import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.api.events.HoloTouchActionLoadEvent;
import com.dsh105.holoapi.api.events.HoloTouchEvent;
import com.dsh105.holoapi.api.touch.TouchAction;
import com.dsh105.holoapi.protocol.Action;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;

/**
 * Example:
 * - Creating and managing touchscreen enabled holograms through the TouchScreen API
 * - On startup, two holograms are created
 * - The first has two touch actions added - one persistent saved under "travel"
 * - The second is touch enabled and handled using the HoloTouchEvent
 * <p/>
 * What you will achieve by following this:
 * - Creating touchable holograms using the TouchScreen API
 * - Add touch actions in two different ways
 * - Add both persistent and temporary touch actions to a hologram
 * - Loading persistent data for hologram touch actions and re-applying to a hologram
 * - Enabling touchscreen functionality on a hologram without adding any touch actions
 * - Using various events to manipulate touchscreen enabled holograms
 */
public class ExampleMain extends JavaPlugin implements Listener {

    private Hologram touchEventHologram;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);

        // Just creating a hologram here. If you don't know how to do this yet, see the other examples
        // NOTE: This hologram will be created on startup every time.
        Hologram hologram = new HologramFactory(this).withLocation(new Location(Bukkit.getWorld("world"), 0, 50, 0)).withText("Click to travel to spawn!").build();

        // This is where we get to the fun stuff
        // This method allows us to add actions that are performed when this hologram is touched (clicked)
        hologram.addTouchAction(new TravelTouchAction(new Location(Bukkit.getWorld("world"), 10, 50, 10)));

        // Another example of how to implement a touch action
        // This one won't be persistent
        hologram.addTouchAction(new TouchAction() {
            @Override
            public void onTouch(Player who, Action action) {
                who.sendMessage("Congrats! You touched a hologram");
            }

            @Override
            public String getSaveKey() {
                // We don't want this action saved, so return null here
                return null;
            }

            @Override
            public LinkedHashMap<String, Object> getDataToSave() {
                // Seeing as the save key is null, this method won't ever be called for this touch action
                return null;
            }
        });

        // Creating and storing another hologram. This will be used later on
        touchEventHologram = new HologramFactory(this).withLocation(new Location(Bukkit.getWorld("world"), 0, 50, 0)).withText("Want some XP?").build();
        // Seeing as we aren't specifically adding any touch actions, this hologram won't be touch enabled yet
        // Consequently, we have to make sure it's enabled
        touchEventHologram.setTouchEnabled(true); // That wasn't too hard was it? :D

        // Once a hologram is touch enabled, HoloAPI won't remove that functionality unless specifically told to using that method

        // As you can see, the possibilities of touchscreens  are limitless using HoloAPI
        // And it's fairly easy too :)
    }

    // This event handles data loading
    // For the hologram above, this won't matter much because it's created on startup anyway
    // For other holograms utilising the custom touch action, this becomes extremely useful for keeping touch actions saved between restarts
    @EventHandler
    public void onDataLoad(HoloTouchActionLoadEvent event) {
        // Check if this is the touch action we wanted
        if (event.getSaveKey().equals("travel")) {
            // Retrieve all the data from the event
            // It would be best to check if the data actually exists first
            // For the purpose of this tutorial, I won't do that here
            int x = (Integer) event.getConfigMap().get("x");
            int y = (Integer) event.getConfigMap().get("y");
            int z = (Integer) event.getConfigMap().get("z");
            World world = Bukkit.getWorld((String) event.getConfigMap().get("world"));

            // Put the touch action back into the hologram that was loaded
            event.getHologram().addTouchAction(new TravelTouchAction(new Location(world, x, y, z)));
        }
    }

    // In this event, we are going to handle the touch actions slightly differently
    // Instead of app
    @EventHandler
    public void onTouch(HoloTouchEvent event) {
        // First check if it's the hologram we want
        if (event.getHologram().equals(this.touchEventHologram)) {
            // Give them XP for touching it and send them a message
            event.getPlayer().giveExpLevels(5);
            event.getPlayer().sendMessage("You just gained 5 XP levels by touching that hologram. Well done!");
        }
    }

    /**
     * Our extension of the TouchAction
     * This will be persistent, using the event above
     */
    public class TravelTouchAction implements TouchAction {

        Location to;

        public TravelTouchAction(Location to) {
            this.to = to;
        }

        @Override
        public void onTouch(Player who, Action action) {
            // No matter who clicks it, teleport them to the location we stored above
            who.teleport(to);
        }

        @Override
        public String getSaveKey() {
            // What do we want to save it as?
            // Might be a good idea to use something more unique than this, especially if other plugins on your server are utilising this API
            // If two actions have the same save key, data will be overwritten
            return "travel";
        }

        @Override
        public LinkedHashMap<String, Object> getDataToSave() {
            // By putting all these values into the map and sending it back to HoloAPI, we can make sure that all the data we need to load it back in is there
            // See the load event above for how to load everything back into holograms
            LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("x", to.getX());
            data.put("y", to.getY());
            data.put("z", to.getZ());
            data.put("world", to.getWorld());
            return data;
        }
    }
}