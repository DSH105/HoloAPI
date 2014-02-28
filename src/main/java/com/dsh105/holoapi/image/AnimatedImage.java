package com.dsh105.holoapi.image;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimatedImage {

    private String key;
    private int frameDelay;
    private ImageGenerator[] imageFrames;
    private int index = 0;

    public AnimatedImage(String key, int frameDelay, ImageGenerator... imageFrames) {
        this.key = key;
        this.frameDelay = frameDelay;
        this.imageFrames = imageFrames;
    }

    public AnimatedImage(String key, int frameDelay, File gifFile, int height, ImageChar imgChar) throws IOException {
        this(key, frameDelay, ImageIO.createImageInputStream(gifFile), height, imgChar);
    }

    public AnimatedImage(String key, int frameDelay, ImageInputStream input, int height, ImageChar imgChar) throws IOException {
        this.key = key;
        this.frameDelay = frameDelay;
        List<BufferedImage> frames = this.getFrames(input);
        imageFrames = new ImageGenerator[frames.size()];
        for (int i = 0; i < frames.size(); i++) {
            imageFrames[i] = new ImageGenerator(frames.get(i), height, imgChar);
        }
    }

    public ArrayList<BufferedImage> getFrames(ImageInputStream imageInputStream) throws IOException {
        ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
        ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
        ir.setInput(ImageIO.createImageInputStream(imageInputStream));
        for (int i = 0; i < ir.getNumImages(true); i++)
            frames.add(ir.read(i));
        return frames;
    }

    public String getKey() {
        return key;
    }

    public int getFrameDelay() {
        return frameDelay;
    }

    public ImageGenerator current() {
        return this.getFrame(index);
    }

    public ImageGenerator next() {
        ++index;
        if (index >= imageFrames.length) {
            index = 0;
            return this.getFrame(index);
        } else {
            return this.getFrame(index);
        }
    }

    public ImageGenerator previous() {
        --index;
        if (index <= 0) {
            index = imageFrames.length - 1;
            return this.getFrame(index);
        } else {
            return this.getFrame(index);
        }
    }

    public ImageGenerator getFrame(int index) {
        return imageFrames[index];
    }
}