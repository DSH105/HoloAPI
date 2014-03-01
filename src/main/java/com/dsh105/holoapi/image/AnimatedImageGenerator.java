package com.dsh105.holoapi.image;

import com.google.common.collect.ImmutableList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.net.www.content.image.gif;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AnimatedImageGenerator implements Generator {

    // Based off: https://github.com/aadnk/DisplayFloatingImages/blob/master/DisplayFloatingImage/src/main/java/com/comphenix/example/nametags/GifImageMessage.java

    private String key;
    protected ImmutableList<GIFFrame> frames;
    private int index = 0;

    public AnimatedImageGenerator(String key, int frameRate, ImageGenerator... imageFrames) {
        this.key = key;
        for (ImageGenerator generator : imageFrames) {
            frames.add(new GIFFrame(generator, frameRate));
        }
    }

    public AnimatedImageGenerator(String key, File gifFile, int frameRate, int height, ImageChar imgChar) throws IOException {
        this(key, gifFile, frameRate, height, imgChar, false);
    }

    public AnimatedImageGenerator(String key, File gifFile, int frameRate, int height, ImageChar imgChar, boolean requiresBorder) throws IOException {
        this.key = key;
        this.frames = this.readGif(gifFile);
        this.prepare(height, imgChar, requiresBorder);
        this.prepareFrameRate(frameRate);
    }

    public AnimatedImageGenerator(String key, File gifFile, int height, ImageChar imgChar) throws IOException {
        this(key, gifFile, height, imgChar, false);
    }

    public AnimatedImageGenerator(String key, File gifFile, int height, ImageChar imgChar, boolean requiresBorder) throws IOException {
        this.key = key;
        this.frames = this.readGif(gifFile);
        this.prepare(height, imgChar, requiresBorder);
    }

    public AnimatedImageGenerator(String key, InputStream input, int frameRate, int height, ImageChar imgChar) throws IOException {
        this(key, input, frameRate, height, imgChar, false);
    }

    public AnimatedImageGenerator(String key, InputStream input, int frameRate, int height, ImageChar imgChar, boolean requiresBorder) throws IOException {
        this.key = key;
        this.frames = this.readGif(input);
        this.prepare(height, imgChar, false);
        this.prepareFrameRate(frameRate);
    }

    public AnimatedImageGenerator(String key, InputStream input, int height, ImageChar imgChar) throws IOException {
        this(key, input, height, imgChar, false);
    }

    public AnimatedImageGenerator(String key, InputStream input, int height, ImageChar imgChar, boolean requiresBorder) throws IOException {
        this.key = key;
        this.frames = this.readGif(input);
        this.prepare(height, imgChar, requiresBorder);
    }

    protected AnimatedImageGenerator(String key) {
        this.key = key;
    }

    protected void prepare(int height, ImageChar imgChar) {
        this.prepare(height, imgChar, false);
    }

    protected void prepare(int height, ImageChar imgChar, boolean requiresBorder) {
        int maxHeight = 0;
        for (GIFFrame frame : frames) {
            maxHeight = Math.max(maxHeight, frame.image.getHeight());
        }

        for (GIFFrame frame : frames) {
            int imageHeight = (int) ((frame.image.getHeight() / (double) maxHeight) * height);
            frame.imageGenerator = new ImageGenerator(frame.image, imageHeight, imgChar, requiresBorder);
        }
    }

    protected void prepareFrameRate(int frameRate) {
        for (GIFFrame frame : this.frames) {
            frame.delay = frameRate;
        }
    }

    protected ImmutableList<GIFFrame> readGif(InputStream input) throws IOException {
        GIFFrame[] frames;

        ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
        reader.setInput(ImageIO.createImageInputStream(input));
        frames = readGIF(reader);

        return ImmutableList.copyOf(frames);
    }

    private ImmutableList<GIFFrame> readGif(File inputFile) throws IOException {
        GIFFrame[] frames;

        ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
        reader.setInput(ImageIO.createImageInputStream(inputFile));
        frames = readGIF(reader);

        return ImmutableList.copyOf(frames);
    }

    // Source: https://stackoverflow.com/questions/8933893/convert-animated-gif-frames-to-separate-bufferedimages-java
    private GIFFrame[] readGIF(ImageReader reader) throws IOException {
        ArrayList<GIFFrame> frames = new ArrayList<GIFFrame>(2);

        int width = -1;
        int height = -1;

        IIOMetadata metadata = reader.getStreamMetadata();
        if (metadata != null) {
            IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

            NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

            if (globalScreenDescriptor != null && globalScreenDescriptor.getLength() > 0) {
                IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreenDescriptor.item(0);

                if (screenDescriptor != null) {
                    width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                    height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                }
            }
        }

        BufferedImage master = null;
        Graphics2D masterGraphics = null;

        for (int frameIndex = 0; ; frameIndex++) {
            BufferedImage image;
            try {
                image = reader.read(frameIndex);
            } catch (IndexOutOfBoundsException io) {
                break;
            }

            if (width == -1 || height == -1) {
                width = image.getWidth();
                height = image.getHeight();
            }

            IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
            int delay = Integer.valueOf(gce.getAttribute("delayTime"));
            String disposal = gce.getAttribute("disposalMethod");

            int x = 0;
            int y = 0;

            if (master == null) {
                master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                masterGraphics = master.createGraphics();
                masterGraphics.setBackground(new Color(0, 0, 0, 0));
            } else {
                NodeList children = root.getChildNodes();
                for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
                    Node nodeItem = children.item(nodeIndex);
                    if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                        NamedNodeMap map = nodeItem.getAttributes();
                        x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
                        y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
                    }
                }
            }
            masterGraphics.drawImage(image, x, y, null);

            BufferedImage copy = new BufferedImage(master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
            frames.add(new GIFFrame(copy, (int) Math.ceil(delay / 2.5D), disposal));

            if (disposal.equals("restoreToPrevious")) {
                BufferedImage from = null;
                for (int i = frameIndex - 1; i >= 0; i--) {
                    if (!frames.get(i).disposal.equals("restoreToPrevious") || frameIndex == 0) {
                        from = frames.get(i).image;
                        break;
                    }
                }

                master = new BufferedImage(from.getColorModel(), from.copyData(null), from.isAlphaPremultiplied(), null);
                masterGraphics = master.createGraphics();
                masterGraphics.setBackground(new Color(0, 0, 0, 0));
            } else if (disposal.equals("restoreToBackgroundColor")) {
                masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
            }
        }
        reader.dispose();

        return frames.toArray(new GIFFrame[frames.size()]);
    }

    @Override
    public String getKey() {
        return key;
    }

    public GIFFrame getCurrent() {
        return this.getFrame(this.index);
    }

    public GIFFrame getNext() {
        if (++this.index >= this.frames.size()) {
            this.index = 0;
        }
        return this.getFrame(this.index);
    }

    public GIFFrame getFrame(int index) {
        if (index >= this.frames.size()) {
            return null;
        }
        return this.frames.get(index);
    }
}