package com.dsh105.holoapi.api;

import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public interface AnimatedHologramFactory {

    /**
     * Sets the location for constructed AnimatedHolograms
     *
     * @param location location for constructed AnimatedHolograms
     * @return This object
     */
    public AnimatedHologramFactory withLocation(Location location);

    /**
     * Sets the location for constructed AnimatedHolograms
     *
     * @param vectorLocation a {@link org.bukkit.util.Vector} representing the coordinates of constructed
     *                       AnimatedHolograms
     * @param worldName      the world name to place constructed AnimatedHolograms in
     * @return This object
     */
    public AnimatedHologramFactory withLocation(Vector vectorLocation, String worldName);

    /**
     * Sets the visibility of constructed Holograms
     *
     * @param visibility visibility of constructed Hologram
     * @return This object
     */
    public AnimatedHologramFactory withVisibility(Visibility visibility);

    /**
     * Adds frames of text to constructed AnimatedHolograms
     * <p/>
     *
     * @param textGenerator text generator used to prepare AnimatedHolograms
     * @return This object
     * @see com.dsh105.holoapi.image.AnimatedTextGenerator
     */
    public AnimatedHologramFactory withText(AnimatedTextGenerator textGenerator);

    /**
     * Adds image frames to constructed AnimatedHolograms
     * <p/>
     *
     * @param animatedImage animation generator used to prepare AnimatedHolograms
     * @return This object
     * @see com.dsh105.holoapi.image.AnimatedImageGenerator
     */
    public AnimatedHologramFactory withImage(AnimatedImageGenerator animatedImage);

    /**
     * /**
     * Adds image frames to constructed AnimatedHolograms
     *
     * @param animatedImageKey key of the animation generator to search for. If a generator is not found, the animation
     *                         will not be added
     * @return This object
     */
    public AnimatedHologramFactory withImage(String animatedImageKey);

    /**
     * Sets the simplicity of constructed AnimatedHolograms
     *
     * @param simple simplicity of constructed AnimatedHolograms
     * @return This object
     */
    public AnimatedHologramFactory withSimplicity(boolean simple);

    /**
     * Constructs an {@link com.dsh105.holoapi.api.AnimatedHologram} based on the settings stored in the factory
     *
     * @return The constructed AnimatedHologram
     * @throws com.dsh105.holoapi.exceptions.HologramNotPreparedException if the animation is empty or the location is
     *                                                                    not initialised
     */
    public AnimatedHologram build();
}
