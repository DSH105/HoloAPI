package com.dsh105.holoapi.exceptions;

public class ImageNotLoadedException extends RuntimeException {

    public ImageNotLoadedException(String customImageKey) {
        super("Image not loaded: " + customImageKey);
    }
}