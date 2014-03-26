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