package com.dsh105.holoapi.api;

import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.image.ImageGenerator;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface HologramFactory {

    public HologramFactory withSaveId(String s);

    public HologramFactory withFirstTagId(int tagId);

    /**
     * Sets the location for constructed Holograms
     *
     * @param location location for constructed Holograms
     * @return This object
     */
    public HologramFactory withLocation(Location location);

    /**
     * Sets the location for constructed Holograms
     *
     * @param vectorLocation a {@link org.bukkit.util.Vector} representing the coordinates of constructed Holograms
     * @param worldName      the world name to place constructed Hologram in
     * @return This object
     */
    public HologramFactory withLocation(Vector vectorLocation, String worldName);

    /**
     * Gets the emptiness state of the stored lines for constructed Holograms
     *
     * @return true if no tags exist
     */
    public boolean isEmpty();

    /**
     * Sets the visibility of constructed Holograms
     *
     * @param visibility visibility of constructed Hologram
     * @return This object
     */
    public HologramFactory withVisibility(Visibility visibility);

    /**
     * Adds text to constructed Holograms
     *
     * @param text Text to add to constructed holograms
     * @return This object
     */
    public HologramFactory withText(String... text);

    /**
     * Clears existing content (text and images) from the factory
     *
     * @return This object
     */
    public HologramFactory clearContent();

    /**
     * Adds an image to constructed Holograms
     * <p/>
     *
     * @param imageGenerator image generator used to prepare Holograms
     * @return This object
     * @throws java.lang.IllegalArgumentException if the generator is null
     * @see com.dsh105.holoapi.image.ImageGenerator
     */
    public HologramFactory withImage(ImageGenerator imageGenerator);

    /**
     * Adds image frames to constructed AnimatedHolograms
     *
     * @param customImageKey key of the image generator to search for. If a generator is not found, the image will not
     *                       be added
     * @return This object
     */
    public HologramFactory withImage(String customImageKey);

    /**
     * Sets the simplicity of constructed Holograms
     *
     * @param simple simplicity of constructed Holograms
     * @return This object
     */
    public HologramFactory withSimplicity(boolean simple);

    /**
     * Constructs an {@link com.dsh105.holoapi.api.Hologram} based on the settings stored in the factory
     *
     * @return The constructed Hologram
     * @throws com.dsh105.holoapi.exceptions.HologramNotPreparedException if the lines are empty or the location is not
     *                                                                    initialised
     */
    public Hologram build();
}
