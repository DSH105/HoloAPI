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

import com.dsh105.holoapi.HoloAPI;
import org.bukkit.ChatColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Represents a generator used to produce images
 * <p/>
 * Original code by @bobacadodl
 * https://forums.bukkit.org/threads/204902/
 */

public class ImageGenerator implements Generator {

    private String lines[];

    private String imageKey;

    private String imageUrl;
    private int imageHeight;
    private ImageChar imageChar = ImageChar.BLOCK;
    private boolean requiresBorder;
    private boolean hasLoaded;

    protected ImageGenerator(BufferedImage image, int height, ImageChar imgChar, boolean requiresBorder) {
        this.imageHeight = height;
        this.imageChar = imgChar;
        this.requiresBorder = requiresBorder;
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), requiresBorder);
    }

    protected ImageGenerator(BufferedImage image, int height, ImageChar imgChar) {
        this.imageHeight = height;
        this.imageChar = imgChar;
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), false);
    }

    /**
     * Constructs an ImageGenerator for use in a Hologram
     *
     * @param imageKey       key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param image          {@link java.awt.image.BufferedImage} used to generate the image
     * @param height         height of the display
     * @param imgChar        {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     */
    public ImageGenerator(String imageKey, BufferedImage image, int height, ImageChar imgChar, boolean requiresBorder) {
        this.imageKey = imageKey;
        this.imageHeight = height;
        this.imageChar = imgChar;
        this.requiresBorder = requiresBorder;
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), requiresBorder);
    }

    /**
     * Constructs an ImageGenerator for use in a Hologram
     *
     * @param imageKey       key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param imageUrl       URL to search the image for
     * @param height         height of the display
     * @param imgChar        {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     */
    public ImageGenerator(String imageKey, String imageUrl, int height, ImageChar imgChar, boolean requiresBorder) {
        this(imageKey, imageUrl, height, imgChar, true, requiresBorder);
    }

    /**
     * Constructs an ImageGenerator for use in a Hologram
     *
     * @param imageKey       key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param imageUrl       URL to search the image for
     * @param height         height of the display
     * @param imgChar        {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param loadUrl        whether to automatically load the image from the specified URL
     * @param requiresBorder whether the display requires a border
     */
    public ImageGenerator(String imageKey, String imageUrl, int height, ImageChar imgChar, boolean loadUrl, boolean requiresBorder) {
        this.imageKey = imageKey;
        this.imageUrl = imageUrl;
        this.imageHeight = height;
        this.imageChar = imgChar;
        this.requiresBorder = requiresBorder;
        if (loadUrl) {
            this.loadUrlImage();
        }
    }

    /**
     * Constructs an ImageGenerator for use in a Hologram
     *
     * @param imageKey       key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param imageFile      file used to generate the image
     * @param height         height of the display
     * @param imgChar        {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     * @throws java.lang.RuntimeException if the image cannot be read
     */
    public ImageGenerator(String imageKey, File imageFile, int height, ImageChar imgChar, boolean requiresBorder) {
        this.imageHeight = height;
        this.imageChar = imgChar;
        this.requiresBorder = requiresBorder;
        BufferedImage image;
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read image " + imageFile.getPath(), e);
        }
        this.imageKey = imageKey;
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), requiresBorder);
    }

    @Override
    public String getKey() {
        return imageKey;
    }

    /**
     * Loads the stored URL data to form a generated image
     *
     * @throws java.lang.IllegalArgumentException if the URL is not initiated
     * @throws java.lang.RuntimeException         if the image cannot be read
     */
    public void loadUrlImage() {
        if (this.hasLoaded) {
            return;
        }
        if (this.imageUrl == null) {
            throw new IllegalArgumentException("URL not initiated for ImageGenerator.");
        }
        URI uri = URI.create(this.imageUrl);
        BufferedImage image;
        try {
            image = ImageIO.read(uri.toURL());
        } catch (IOException e) {
            throw new RuntimeException("Cannot read image " + uri, e);
        }
        this.lines = this.generate(generateColours(image, this.imageHeight), this.imageChar.getImageChar(), requiresBorder);
        this.hasLoaded = true;
    }

    /**
     * Gets the generated lines
     *
     * @return generated lines
     */
    public String[] getLines() {
        return lines;
    }

    private String[] generate(ChatColor[][] colors, char imgchar, boolean border) {
        String[] lines = new String[colors[0].length];
        for (int y = 0; y < colors[0].length; y++) {
            String line = "";
            for (ChatColor[] color : colors) {
                ChatColor colour = color[y];
                if (colour == null) {
                    line += border ? HoloAPI.getTransparencyWithBorder() : HoloAPI.getTransparencyWithoutBorder();
                } else {
                    line += colour.toString() + imgchar + ChatColor.RESET;
                }
            }

            if (!border) {
                line = line.trim();
            }

            lines[y] = line + ChatColor.RESET;
        }
        return lines;
    }

    private ChatColor[][] generateColours(BufferedImage image, int height) {
        double ratio = (double) image.getHeight() / image.getWidth();
        BufferedImage resized = resize(image, (int) (height / ratio), height);

        ChatColor[][] chatImg = new ChatColor[resized.getWidth()][resized.getHeight()];
        for (int x = 0; x < resized.getWidth(); x++) {
            for (int y = 0; y < resized.getHeight(); y++) {
                int rgb = resized.getRGB(x, y);
                ChatColor closest = ColourMap.getClosest(new Color(rgb, true));
                chatImg[x][y] = closest;
            }
        }
        return chatImg;
    }

    private BufferedImage resize(BufferedImage originalImage, int width, int height) {
        AffineTransform af = new AffineTransform();
        af.scale(width / (double) originalImage.getWidth(), height / (double) originalImage.getHeight());
        AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(originalImage, null);
    }

}