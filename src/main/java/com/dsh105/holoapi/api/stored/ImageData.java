package com.dsh105.holoapi.api.stored;

import com.dsh105.holoapi.image.ImageChar;

public class ImageData implements DataStorage {

    private String imagePath;
    private int imageHeight;
    private ImageChar imageChar;

    public ImageData(String imagePath, int imageHeight, ImageChar imageChar) {
        this.imagePath = imagePath;
        this.imageHeight = imageHeight;
        this.imageChar = imageChar;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public ImageChar getImageChar() {
        return imageChar;
    }

    public void setImageChar(ImageChar imageChar) {
        this.imageChar = imageChar;
    }
}