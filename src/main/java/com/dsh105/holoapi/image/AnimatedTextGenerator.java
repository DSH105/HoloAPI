package com.dsh105.holoapi.image;

import java.util.ArrayList;

public class AnimatedTextGenerator {

    private int maxHeight;
    private ArrayList<Frame> frames = new ArrayList<Frame>();
    private Frame largestFrame;

    public AnimatedTextGenerator(Frame[] frames) {
        this.largestFrame = frames[0];
        for (Frame f : frames) {
            this.frames.add(f);
        }
        this.calculateMaxHeight();

        for (Frame f : this.frames) {
            int diff = this.maxHeight - f.getLines().length;
            if (diff > 0) {
                ArrayList<String> lines = new ArrayList<String>();
                for (String s : f.getLines()) {
                    lines.add(s);
                }
                for (int i = 0; i <= diff; i++) {
                    lines.add(" ");
                }
                f.setLines(lines.toArray(new String[lines.size()]));
            }
        }
    }

    protected void calculateMaxHeight() {
        int maxHeight = 0;
        Frame largestFrame = this.frames.get(0);
        for (Frame frame : frames) {
            if (frame.getLines().length > maxHeight) {
                maxHeight = frame.getLines().length;
                largestFrame = frame;
            }
        }
        this.maxHeight = maxHeight;
        this.largestFrame = largestFrame;
    }

    public Frame getLargestFrame() {
        return largestFrame;
    }

    public ArrayList<Frame> getFrames() {
        return frames;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}