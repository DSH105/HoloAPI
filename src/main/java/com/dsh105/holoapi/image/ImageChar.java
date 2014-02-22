package com.dsh105.holoapi.image;

public enum ImageChar {

    BLOCK('\u2588'),
    DARK_SHADE('\u2593'),
    MEDIUM_SHADE('\u2592'),
    LIGHT_SHADE('\u2591');

    private char imageChar;

    ImageChar(char imageChar) {
        this.imageChar = imageChar;
    }

    public char getImageChar() {
        return imageChar;
    }
}