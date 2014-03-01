package com.dsh105.holoapi.image;

import org.bukkit.command.CommandSender;

public interface ImageLoader<T extends Generator> {

    public T getGenerator(CommandSender sender, String key);

    public T getGenerator(String key);

    public boolean exists(String key);

    public boolean isLoaded();

    public enum ImageLoadType {
        URL, FILE;
    }
}