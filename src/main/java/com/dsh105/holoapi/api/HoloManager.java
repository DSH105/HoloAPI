package com.dsh105.holoapi.api;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface HoloManager {

    /**
     * Gets all the stored holograms, including simple holograms
     * <p/>
     * Simple holograms include chat bubbles and indicators if the server has them enabled
     *
     * @return all stored holograms
     */
    public HashMap<Hologram, Plugin> getAllHolograms();

    /**
     * Gets all the stored holograms which are not simple
     *
     * @return all stored holograms
     */
    public HashMap<Hologram, Plugin> getAllComplexHolograms();

    /**
     * Gets all the stored holograms which are simple
     * <p/>
     * Simple holograms include chat bubbles and indicators if the server has them enabled
     *
     * @return all stored holograms
     */
    public HashMap<Hologram, Plugin> getAllSimpleHolograms();

    /**
     * Gets all stored holograms for a plugin
     *
     * @param owningPlugin plugin to retrieve holograms for
     * @return holograms registered under a plugin
     */
    public ArrayList<Hologram> getHologramsFor(Plugin owningPlugin);

    /**
     * Gets a hologram of an ID
     *
     * @param hologramId hologram ID to search with
     * @return hologram of an ID
     */
    public Hologram getHologram(String hologramId);

    /**
     * Tracks and registers a hologram
     *
     * @param hologram     hologram to register
     * @param owningPlugin plugin to register hologram under
     */
    public void track(Hologram hologram, Plugin owningPlugin);

    /**
     * Stops tracking a hologram and clears all player views
     * <p/>
     * This does not clear the hologram data from file
     *
     * @param hologram hologram to stop tracking
     */
    public void stopTracking(Hologram hologram);

    /**
     * Stops tracking a hologram and clears all player views
     * <p/>
     * This does not clear the hologram data from file
     *
     * @param hologramId ID of hologram to stop tracking
     */
    public void stopTracking(String hologramId);

    /**
     * Saves hologram data to file
     *
     * @param hologramId ID of hologram to save
     */
    public void saveToFile(String hologramId);

    /**
     * Saves hologram data to file
     *
     * @param hologram hologram to save
     */
    public void saveToFile(Hologram hologram);

    /**
     * Clears hologram data from file
     *
     * @param hologramId ID of hologram to clear
     */
    public void clearFromFile(String hologramId);

    /**
     * Clears hologram data from file
     *
     * @param hologram hologram to clear
     */
    public void clearFromFile(Hologram hologram);

    /**
     * Copies a hologram from the provided original
     *
     * @param original     original hologram
     * @param copyLocation location to copy the hologram to
     * @return copied hologram
     */
    public Hologram copy(Hologram hologram, Location copyLocation);

    /**
     * Creates a simple hologram. Simple holograms are automatically removed after a certain period of time, are not saved to file and can only include text content.
     *
     * @param location            position to place the simple hologram
     * @param secondsUntilRemoved time in seconds until the hologram is removed
     * @param lines               content of the hologram
     * @return The constructed simple Hologram
     */
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, List<String> lines);

    /**
     * Creates a simple hologram. Simple holograms are automatically removed after a certain period of time, are not saved to file and can only include text content.
     *
     * @param location            position to place the simple hologram
     * @param secondsUntilRemoved time in seconds until the hologram is removed
     * @param lines               content of the hologram
     * @return The constructed simple hologram
     */
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, String... lines);

    /**
     * Creates a simple hologram. Simple holograms are automatically removed after a certain period of time, are not saved to file and can only include text content.
     *
     * @param location            position to place the simple hologram
     * @param secondsUntilRemoved time in seconds until the hologram is removed
     * @param rise                if true, the hologram will automatically rise upwards at a predefined speed
     * @param lines               content of the hologram
     * @return The constrctued simple Hologram
     */
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, boolean rise, List<String> lines);

    /**
     * Creates a simple hologram. Simple holograms are automatically removed after a certain period of time, are not saved to file and can only include text content.
     *
     * @param location            position to place the simple hologram
     * @param secondsUntilRemoved time in seconds until the hologram is removed
     * @param rise                if true, the hologram will automatically rise upwards at a predefined speed
     * @param lines               content of the hologram
     * @return The constucted simple Hologram
     */
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, boolean rise, String... lines);

    /**
     * Creates a simple hologram. Simple holograms are automatically removed after a certain period of time, are not saved to file and can only include text content.
     *
     * @param location            position to place the simple hologram
     * @param secondsUntilRemoved time in seconds until the hologram is removed
     * @param velocity            velocity to apply to the hologram
     * @param lines               content of the hologram
     * @return The constructed simple Hologram
     */
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, Vector velocity, List<String> lines);

    /**
     * Creates a simple hologram. Simple holograms are automatically removed after a certain period of time, are not saved to file and can only include text content.
     *
     * @param location            position to place the simple hologram
     * @param secondsUntilRemoved time in seconds until the hologram is removed
     * @param velocity            velocity to apply to the hologram
     * @param lines               content of the hologram
     * @return The constructed simple Hologram
     */
    public Hologram createSimpleHologram(Location location, int secondsUntilRemoved, final Vector velocity, String... lines);
}