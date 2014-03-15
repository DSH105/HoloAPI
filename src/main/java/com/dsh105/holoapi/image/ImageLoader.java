package com.dsh105.holoapi.image;

import org.bukkit.command.CommandSender;

/**
 * Represents an image loader used to store pre-loaded images and animations from the HoloAPI Configuration file
 *
 * @param <T> type of generator
 */

public interface ImageLoader<T extends Generator> {

    /**
     * Gets a loaded generator. If the generator found has a URL type and has not yet been loaded, the loading process will be started and the method will return null
     * <p/>
     * Also sends a message to the {@link org.bukkit.command.CommandSender} if the URL image is being loaded
     *
     * @param sender sender to send the URL loading message to
     * @param key    key to search for a generator with
     * @return Image or Animation loader loaded with HoloAPI. Returns null if the image generator is not found or a URL generator is being loaded
     */
    public T getGenerator(CommandSender sender, String key);

    /**
     * Gets a loaded generator. If the generator found has a URL type and has not yet been loaded, the loading process will be started and the method will return null
     *
     * @param key key to search for a generator with
     * @return Image or Animation loader loaded with HoloAPI. Returns null if the image generator is not found or a URL generator is being loaded
     */
    public T getGenerator(String key);

    /**
     * Checks and returns whether a generator of a key exists. This does NOT check for unloaded URL generators
     *
     * @param key key to search for a generator with
     * @return true if the generator exists
     */
    public boolean exists(String key);

    /**
     * Checks and returns whether an unloaded URL generator of a key exists. This ONLY checks for UNLOADED URL generators
     *
     * @param key key to search for a generator with
     * @return true if the unloaded URL generator exists
     */
    public boolean existsAsUnloadedUrl(String key);

    /**
     * Gets whether this loader has finished loading all generators
     *
     * @return true if finished loading
     */
    public boolean isLoaded();

    /**
     * Represents the type of image being loaded
     */
    public enum ImageLoadType {
        /**
         * Represents a URL image that is retrieved from a HTTP address
         */
        URL,

        /**
         * Represents an image that can be located within the HoloAPI plugin data folder
         */
        FILE;
    }
}