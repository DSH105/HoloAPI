package com.dsh105.holoapi.image;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a generator used to produce animated text frames
 */

public class AnimatedTextGenerator {

    private int maxHeight;
    private ArrayList<Frame> frames = new ArrayList<Frame>();
    private Frame largestFrame;

    /**
     * Constructs an AnimatedTextGenerator for use in an AnimatedHologram
     *
     * @param frames frames used to construct the generator with
     */
    public AnimatedTextGenerator(Frame[] frames) {
        this.largestFrame = frames[0];
        Collections.addAll(this.frames, frames);
        this.calculateMaxHeight();

        for (Frame f : this.frames) {
            int diff = this.maxHeight - f.getLines().length;
            if (diff > 0) {
                ArrayList<String> lines = new ArrayList<String>();
                Collections.addAll(lines, f.getLines());
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

    /**
     * Gets the largest frame of the generator
     *
     * @return largest frame of the generator
     */
    public Frame getLargestFrame() {
        return largestFrame;
    }

    /**
     * Gets the frames of the generator
     *
     * @return frames of the generator
     */
    public ArrayList<Frame> getFrames() {
        return frames;
    }

    /**
     * Gets the maximum frame height
     *
     * @return maximum frame height
     */
    public int getMaxHeight() {
        return maxHeight;
    }
}