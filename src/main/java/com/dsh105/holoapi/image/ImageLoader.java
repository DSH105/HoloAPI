package com.dsh105.holoapi.image;

public interface ImageLoader {

    public ImageGenerator getGenerator(String key);

    public enum ImageLoadType {
        URL, FILE;
    }
}