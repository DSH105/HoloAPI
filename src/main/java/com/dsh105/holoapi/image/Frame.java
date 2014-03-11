package com.dsh105.holoapi.image;

import com.dsh105.holoapi.util.UnicodeFormatter;

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
                lines[i] = UnicodeFormatter.replaceAll(lines[i]);
            }
            this.lines = lines;
        }
    }
}