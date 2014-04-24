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

import com.google.common.collect.ImmutableList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

/**
 * Represents a generator used to produce animated image frames from either a GIF or set of images
 */

public class AnimatedImageGenerator implements Generator {

    // https://github.com/aadnk/DisplayFloatingImages/blob/master/DisplayFloatingImage/src/main/java/com/comphenix/example/nametags/GifImageMessage.java

    protected ImmutableList<GIFFrame> frames;
    private String key;
    private int maxHeight;
    private GIFFrame largestFrame;

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key         key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param frameRate   frame rate of the hologram display
     * @param imageFrames frames to use in the animated hologram
     */
    public AnimatedImageGenerator(String key, int frameRate, ImageGenerator... imageFrames) {
        this.key = key;
        for (ImageGenerator generator : imageFrames) {
            frames.add(new GIFFrame(generator, frameRate));
        }
    }

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key       key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param gifFile   GIF file used to generate the display
     * @param frameRate frame rate of the hologram display
     * @param height    height of the display
     * @param imgChar   {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @throws IOException If an input exception occurred or the image could not be found
     */
    public AnimatedImageGenerator(String key, File gifFile, int frameRate, int height, ImageChar imgChar) throws IOException {
        this(key, gifFile, frameRate, height, imgChar, false);
    }

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key            key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param gifFile        GIF file used to generate the display
     * @param frameRate      frame rate of the hologram display
     * @param height         height of the display
     * @param imgChar        {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     * @throws IOException If an input exception occurred or the image could not be found
     */
    public AnimatedImageGenerator(String key, File gifFile, int frameRate, int height, ImageChar imgChar, boolean requiresBorder) throws IOException {
        this.key = key;
        this.frames = this.readGif(gifFile);
        this.prepare(height, imgChar, requiresBorder);
        this.prepareFrameRate(frameRate);
    }

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key     key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param gifFile GIF file used to generate the display
     * @param height  height of the display
     * @param imgChar {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @throws IOException If an input exception occurred or the image could not be found
     */
    public AnimatedImageGenerator(String key, File gifFile, int height, ImageChar imgChar) throws IOException {
        this(key, gifFile, height, imgChar, false);
    }

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key            key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param gifFile        GIF file used to generate the display
     * @param height         height of the display
     * @param imgChar        {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     * @throws IOException If an input exception occurred or the image could not be found
     */
    public AnimatedImageGenerator(String key, File gifFile, int height, ImageChar imgChar, boolean requiresBorder) throws IOException {
        this.key = key;
        this.frames = this.readGif(gifFile);
        this.prepare(height, imgChar, requiresBorder);
    }

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key       key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param input     {@link java.io.InputStream} used to generate the display
     * @param frameRate frame rate of the hologram display
     * @param height    height of the display
     * @param imgChar   {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @throws IOException If an input exception occurred or the image could not be found
     */
    public AnimatedImageGenerator(String key, InputStream input, int frameRate, int height, ImageChar imgChar) throws IOException {
        this(key, input, frameRate, height, imgChar, false);
    }

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key            key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param input          {@link java.io.InputStream} used to generate the display
     * @param frameRate      frame rate of the hologram display
     * @param height         height of the display
     * @param imgChar        {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     * @throws IOException If an input exception occurred or the image could not be found
     */
    public AnimatedImageGenerator(String key, InputStream input, int frameRate, int height, ImageChar imgChar, boolean requiresBorder) throws IOException {
        this.key = key;
        this.frames = this.readGif(input);
        this.prepare(height, imgChar, requiresBorder);
        this.prepareFrameRate(frameRate);
    }

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key     key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param input   {@link java.io.InputStream} used to generate the display
     * @param height  height of the display
     * @param imgChar {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @throws IOException If an input exception occurred or the image could not be found
     */
    public AnimatedImageGenerator(String key, InputStream input, int height, ImageChar imgChar) throws IOException {
        this(key, input, height, imgChar, false);
    }

    /**
     * Constructs an AnimatedImageGenerator for use in an AnimatedHologram
     *
     * @param key            key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param input          {@link java.io.InputStream} used to generate the display
     * @param height         height of the display
     * @param imgChar        {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     * @throws IOException If an input exception occurred or the image could not be found
     */
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
        this.calculateMaxHeight();
        for (GIFFrame frame : frames) {
            int imageHeight = (int) ((frame.image.getHeight() / (double) this.maxHeight) * height);
            frame.imageGenerator = new ImageGenerator(frame.image, imageHeight, imgChar, requiresBorder);
        }
    }

    protected void prepareFrameRate(int frameRate) {
        for (GIFFrame frame : this.frames) {
            frame.delay = frameRate;
        }
    }

    protected void calculateMaxHeight() {
        int maxHeight = 0;
        GIFFrame largestFrame = this.frames.get(0);
        for (GIFFrame frame : frames) {
            if (frame.image.getHeight() > maxHeight) {
                maxHeight = frame.image.getHeight();
                largestFrame = frame;
            }
        }
        this.maxHeight = maxHeight;
        this.largestFrame = largestFrame;
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

    /**
     * Gets the frames of the generator
     *
     * @return frames of the generator
     */
    public ArrayList<GIFFrame> getFrames() {
        return new ArrayList<GIFFrame>(this.frames);
    }

    /**
     * Gets the maximum frame height
     *
     * @return maximum frame height
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * Gets the largest frame of the generator
     *
     * @return largest frame of the generator
     */
    public GIFFrame getLargestFrame() {
        return largestFrame;
    }
}
