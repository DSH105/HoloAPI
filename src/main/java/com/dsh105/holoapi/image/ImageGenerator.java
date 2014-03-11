package com.dsh105.holoapi.image;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import javax.imageio.ImageIO;
import org.bukkit.ChatColor;

/**
 * Represents a generator used to produce images
 *
 * Original code by @bobacadodl
 * https://forums.bukkit.org/threads/204902/
 */

public class ImageGenerator implements Generator {

    private final static char TRANSPARENT_CHAR_NOBORDER = ' ';
    private final static String TRANSPARENT_CHAR_BORDER = "  ";
    //private final static char TRANSPARENT_CHAR_BORDER = ImageChar.LIGHT_SHADE.getImageChar();

    private String lines[];

    private String imageKey;

    private String storedImageUrl;

    private int storedImageHeight;
    private ImageChar storedImgChar = ImageChar.BLOCK;
    private boolean storedRequiresBorder;

    protected ImageGenerator(BufferedImage image, int height, ImageChar imgChar, boolean requiresBorder) {
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), requiresBorder);
    }

    protected ImageGenerator(BufferedImage image, int height, ImageChar imgChar) {
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), false);
    }

    /**
     * Constructs an ImageGenerator for use in a Hologram
     *
     * @param imageKey key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param image {@link java.awt.image.BufferedImage} used to generate the image
     * @param height height of the display
     * @param imgChar {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     */
    public ImageGenerator(String imageKey, BufferedImage image, int height, ImageChar imgChar, boolean requiresBorder) {
        this.imageKey = imageKey;
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), requiresBorder);
    }

    /**
     * Constructs an ImageGenerator for use in a Hologram
     *
     * @param imageKey key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param imageUrl URL to search the image for
     * @param height height of the display
     * @param imgChar {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     */
    public ImageGenerator(String imageKey, String imageUrl, int height, ImageChar imgChar, boolean requiresBorder) {
        this(imageKey, imageUrl, height, imgChar, true, requiresBorder);
    }

    /**
     * Constructs an ImageGenerator for use in a Hologram
     *
     * @param imageKey key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param imageUrl URL to search the image for
     * @param height height of the display
     * @param imgChar {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param loadUrl whether to automatically load the image from the specified URL
     * @param requiresBorder whether the display requires a border
     */
    public ImageGenerator(String imageKey, String imageUrl, int height, ImageChar imgChar, boolean loadUrl, boolean requiresBorder) {
        this.imageKey = imageKey;
        if (loadUrl) {
            this.loadUrlImage();
        } else {
            this.storedImageUrl = imageUrl;
            this.storedImageHeight = height;
            this.storedImgChar = imgChar;
            this.storedRequiresBorder = requiresBorder;
        }
    }

    /**
     * Constructs an ImageGenerator for use in a Hologram
     *
     * @param imageKey key to store the generator under. This MAY cause some issues with saving if the generator settings are not stored in a HoloAPI configuration file
     * @param imageFile file used to generate the image
     * @param height height of the display
     * @param imgChar {@link com.dsh105.holoapi.image.ImageChar} of the display
     * @param requiresBorder whether the display requires a border
     */
    public ImageGenerator(String imageKey, File imageFile, int height, ImageChar imgChar, boolean requiresBorder) {
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

    protected void loadUrlImage() {
        if (this.storedImageUrl == null) {
            throw new NullPointerException("URL not initiated for ImageGenerator.");
        }
        URI uri = URI.create(this.storedImageUrl);
        BufferedImage image;
        try {
            image = ImageIO.read(uri.toURL());
        } catch (IOException e) {
            throw new RuntimeException("Cannot read image " + uri, e);
        }
        this.lines = this.generate(generateColours(image, this.storedImageHeight), this.storedImgChar.getImageChar(), storedRequiresBorder);
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
            for (int x = 0; x < colors.length; x++) {
                ChatColor colour = colors[x][y];
                if (colour == null) {
                    line += border ? TRANSPARENT_CHAR_BORDER : TRANSPARENT_CHAR_NOBORDER;
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
        int width = (int) (height / ratio);
        if (width > 10) width = 10;
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