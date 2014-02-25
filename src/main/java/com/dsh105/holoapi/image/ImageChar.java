package com.dsh105.holoapi.image;

public enum ImageChar {

    BLOCK('\u2588', "block"),
    DARK_SHADE('\u2593', "dark"),
    MEDIUM_SHADE('\u2592', "medium"),
    LIGHT_SHADE('\u2591', "light");

    private char imageChar;
    private String humanName;

    ImageChar(char imageChar, String humanName) {
        this.imageChar = imageChar;
        this.humanName = humanName;
    }

    public char getImageChar() {
        return imageChar;
    }

    public String getHumanName() {
        return humanName;
    }

    public static ImageChar fromHumanName(String humanName, boolean ignoreCase) {
        for (ImageChar c : ImageChar.values()) {
            if (c.getHumanName().equals(ignoreCase ? humanName.toLowerCase() : humanName)) {
                return c;
            }
        }
        return null;
    }
}