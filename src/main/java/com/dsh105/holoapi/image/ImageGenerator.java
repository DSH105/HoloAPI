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
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), b);
    }

    protected ImageGenerator(BufferedImage image, int height, ImageChar imgChar) {
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), false);
    }

    public ImageGenerator(String imageKey, BufferedImage image, int height, ImageChar imgChar, boolean requiresBorder) {
        this.imageKey = imageKey;
        this.lines = this.generate(generateColours(image, height), imgChar.getImageChar(), requiresBorder);
    }

    public ImageGenerator(String imageKey, String imageUrl, int height, ImageChar imgChar, boolean requiresBorder) {
        this(imageKey, imageUrl, height, imgChar, true, requiresBorder);
    }

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

    public String[] getLines() {
        return lines;
    }

    private String[] generate(ChatColor[][] colors, char imgchar, boolean b) {
        String[] lines = new String[colors[0].length];
        for (int y = 0; y < colors[0].length; y++) {
            String line = "";
            for (int x = 0; x < colors.length; x++) {
                ChatColor colour = colors[x][y];
                if (colour == null) {
                    line += b ? TRANSPARENT_CHAR_BORDER : TRANSPARENT_CHAR_NOBORDER;
                    /*if (!line.equals("")) {
                        line += TRANSPARENT_CHAR;
                    }*/
                } else {
                    line += colour.toString() + imgchar + ChatColor.RESET;
                }
            }

            if (!b) {
                line = line.trim();
            }

            lines[y] = line + ChatColor.RESET;
        }
        /*int min = 0;
        for (String s : lines) {
            if (s != null) {
                char[] chars = s.toCharArray();
                char space = ' ';
                int j = 0;
                for (int i = s.length() - 1; i > 0; i--) {
                    if (chars[i] == space) {
                        j++;
                    }
                    if (j < min) {
                        min = j;
                    }
                }
            }
        }

        String[] newLines = new String[lines.length];
        int index = 0;
        for (String s : lines) {
            if (s != null) {
                newLines[index] = s.substring(0, s.length() - min);
            }
        }*/
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