package com.dsh105.holoapi.image;

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
 * Credit to bobacadodl for this one <3
 */

public class ImageGenerator {

    private final static String TRANSPARENT_CHAR = /*'\u2591'*/"  ";

    private String lines[];

    private String storedImageUrl;
    private int sotredImageHeight;
    private ImageChar storedImgChar = ImageChar.BLOCK;

    public ImageGenerator(BufferedImage image, int height, ImageChar imgChar) {
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar());
    }

    public ImageGenerator(String imageUrl, int height, ImageChar imgChar) {
        this(imageUrl, height, imgChar, true);
    }

    public ImageGenerator(String imageUrl, int height, ImageChar imgChar, boolean loadUrl) {
        if (loadUrl) {
            this.loadUrlImage();
        } else {
            this.storedImageUrl = imageUrl;
            this.sotredImageHeight = height;
            this.storedImgChar = imgChar;
        }
    }

    public ImageGenerator(File imageFile, int height, ImageChar imgChar) {
        BufferedImage image;
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException("Cannot read image " + imageFile.getPath(), e);
        }
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar());
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
        this.lines = this.generate(generateColours(image, this.sotredImageHeight), this.storedImgChar.getImageChar());
    }

    public String[] getLines() {
        return lines;
    }

    private String[] generate(ChatColor[][] colors, char imgchar) {
        String[] lines = new String[colors[0].length];
        for (int y = 0; y < colors[0].length; y++) {
            String line = "";
            for (int x = 0; x < colors.length; x++) {
                ChatColor colour = colors[x][y];
                if (colour == null) {
                    //line += TRANSPARENT_CHAR;
                    if (!line.equals("")) {
                        line += TRANSPARENT_CHAR;
                    }
                } else {
                    line += colour.toString() + imgchar;
                }
            }

            line = line.trim();

            /*for (int x = colors.length - 1; x > 0; x--) {
                if (line.length() > 0) {
                    if (!(colors[x][y] == null || colors[x][y].equals(TRANSPARENT_CHAR))) {
                        break;
                    } else {
                        line = line.substring(0, line.length() - 1);
                    }
                } else {
                    break;
                }
            }*/
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
        af.scale(width / (double)originalImage.getWidth(), height / (double)originalImage.getHeight());
        AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(originalImage, null);
        /*BufferedImage resizedImage = new BufferedImage(width, height, 6);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return resizedImage;*/
    }

}