package com.dsh105.holoapi.image;

import org.bukkit.command.CommandSender;

public interface ImageLoader {

    public ImageGenerator getGenerator(CommandSender sender, String key);

    public ImageGenerator getGenerator(String key);

    public boolean exists(String key);

    public boolean isLoaded();

    public enum ImageLoadType {
        URL, FILE;
    }
}