package com.dsh105.holoapi.image;

public interface Generator {

    /**
     * Gets the key of the generator. The key is defined in the HoloAPI Configuration file by the server operator and is registered on startup
     *
     * @return key of the generator
     */
    public String getKey();
}