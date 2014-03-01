package com.dsh105.holoapi.image;

public class UnloadedImageStorage {

    private String imagePath;
    private int imageHeight;
    private int frameRate;
    private ImageChar charType;
    private boolean requiresBorder;

    public UnloadedImageStorage(String imagePath, int imageHeight, ImageChar charType, boolean requiresBorder) {
        this.imagePath = imagePath;
        this.imageHeight = imageHeight;
        this.charType = charType;
        this.requiresBorder = requiresBorder;
    }

    public UnloadedImageStorage(String imagePath, int imageHeight, int frameRate, ImageChar charType, boolean requiresBorder) {
        this.imagePath = imagePath;
        this.imageHeight = imageHeight;
        this.frameRate = frameRate;
        this.charType = charType;
        this.requiresBorder = requiresBorder;
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

    public ImageChar getCharType() {
        return charType;
    }

    public void setCharType(ImageChar charType) {
        this.charType = charType;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    public boolean requiresBorder() {
        return requiresBorder;
    }

    public void setRequiresBorder(boolean requiresBorder) {
        this.requiresBorder = requiresBorder;
    }
}