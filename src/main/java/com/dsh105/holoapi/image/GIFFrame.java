package com.dsh105.holoapi.image;

import java.awt.image.BufferedImage;

public class GIFFrame {

    protected BufferedImage image;
    protected int delay;
    protected String disposal;

    protected ImageGenerator imageGenerator;

    protected GIFFrame(BufferedImage image, int delay, String disposal) {
        this.image = image;
        this.delay = delay;
        this.disposal = disposal;
    }

    protected GIFFrame(ImageGenerator generator, int delay) {
        this.delay = delay;
        this.imageGenerator = generator;
    }

    public int getDelay() {
        return delay;
    }

    public ImageGenerator getImageGenerator() {
        return imageGenerator;
    }
}