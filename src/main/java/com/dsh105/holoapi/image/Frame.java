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

/**
 * Represents a text or image frame used in AnimatedImageGenerators
 */

public class Frame {

    private String[] lines;

    protected int delay;

    /**
     * Constructs a new Frame
     *
     * @param delay delay before the next frame is displayed
     * @param lines lines of the frame
     */
    public Frame(int delay, String... lines) {
        this.delay = delay;
        this.setLines(lines);
    }

    /**
     * Gets the delay of the frame before the next is displayed
     *
     * @return delay of the frame
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Gets the lines of the frame
     *
     * @return lines of the frame
     */
    public String[] getLines() {
        return this.lines;
    }

    protected void setLines(String[] lines) {
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                lines[i] = lines[i];
            }
            this.lines = lines;
        }
    }
}