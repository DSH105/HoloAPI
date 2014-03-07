package com.dsh105.holoapi.image;

import java.awt.image.BufferedImage;

public class GIFFrame extends Frame {

    protected BufferedImage image;
    protected String disposal;

    protected ImageGenerator imageGenerator;

    protected GIFFrame(BufferedImage image, int delay, String disposal) {
        super(delay);
        this.image = image;
        this.disposal = disposal;
    }

    protected GIFFrame(ImageGenerator generator, int delay) {
        super(delay);
        this.imageGenerator = generator;
    }

    public ImageGenerator getImageGenerator() {
        return imageGenerator;
    }

    @Override
    public String[] getLines() {
        return this.imageGenerator.getLines();
    }
}