/*
 * This file is part of HoloAPI.
 *
 * HoloAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoloAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoloAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dsh105.holoapi.image;

public enum ImageChar {

    /**
     * Represents a filled block character
     */
    BLOCK('\u2588', "block"),

    /**
     * Represents a dark shaded character
     */
    DARK_SHADE('\u2593', "dark"),

    /**
     * Represents a half shaded character
     */
    MEDIUM_SHADE('\u2592', "medium"),

    /**
     * Represents a lightly shaded character
     */
    LIGHT_SHADE('\u2591', "light");

    private char imageChar;
    private String humanName;

    ImageChar(char imageChar, String humanName) {
        this.imageChar = imageChar;
        this.humanName = humanName;
    }

    /**
     * Gets the character of the ImageChar
     *
     * @return character of the ImageChar
     */
    public char getImageChar() {
        return imageChar;
    }

    /**
     * Gets the friendly name
     *
     * @return friendly name
     */
    public String getHumanName() {
        return humanName;
    }

    /**
     * Gets the appropriate ImageChar from its friendly name
     *
     * @param humanName friendly name used to search for
     * @return ImageChar if found, null if it doesn't exist
     */
    public static ImageChar fromHumanName(String humanName) {
        for (ImageChar c : ImageChar.values()) {
            if (c.getHumanName().equalsIgnoreCase(humanName)) {
                return c;
            }
        }
        return null;
    }
}