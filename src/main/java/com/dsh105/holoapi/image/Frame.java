package com.dsh105.holoapi.image;

import com.dsh105.holoapi.util.UnicodeFormatter;

public class Frame {

    private String[] lines;

    protected int delay;

    public Frame(int delay, String... lines) {
        this.delay = delay;
        this.setLines(lines);
    }

    public int getDelay() {
        return delay;
    }

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