package com.dsh105.holoapi.api;

import com.dsh105.holoapi.api.stored.DataStorage;
import com.dsh105.holoapi.api.stored.ImageData;
import com.dsh105.holoapi.api.stored.TextData;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.image.ImageChar;
import com.dsh105.holoapi.image.ImageGenerator;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HologramFactory {

    private double locX;
    private double locY;
    private double locZ;
    private boolean prepared = false;

    private HashMap<DataStorage, TagType> tags = new HashMap<DataStorage, TagType>();

    public HologramFactory withLocation(Location location) {
        return this.withCoords(location.getX(), location.getY(), location.getZ());
    }

    public HologramFactory withCoords(double x, double y, double z) {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        this.prepared = true;
        return this;
    }

    public HologramFactory withText(String... text) {
        for (String tag : text) {
            this.tags.put(new TextData(text), TagType.TEXT);
        }
        return this;
    }

    public HologramFactory withImage(String imagePath, int imageHeight) {
        this.tags.put(new ImageData(imagePath, imageHeight, ImageChar.MEDIUM_SHADE), TagType.IMAGE_PATH);
        return this;
    }

    public HologramFactory withImage(String imagePath, int imageHeight, ImageChar imageChar) {
        this.tags.put(new ImageData(imagePath, imageHeight, imageChar), TagType.IMAGE_PATH);
        return this;
    }

    public HologramFactory withImage(ImageData image) {
        this.tags.put(image, TagType.IMAGE_PATH);
        return this;
    }

    public Hologram build() {
        if (this.tags.isEmpty()) {
            throw new HologramNotPreparedException("Hologram lines cannot be empty.");
        }
        if (!this.prepared) {
            throw new HologramNotPreparedException("Hologram location cannot be null.");
        }
        ArrayList<String> tags = new ArrayList<String>();
        for (Map.Entry<DataStorage, TagType> entry : this.tags.entrySet()) {
            if (entry.getValue() == TagType.TEXT) {
                TextData textData = (TextData) entry.getKey();
                for (String text : textData.getText()) {
                    tags.add(text);
                }
            } else if (entry.getValue() == TagType.IMAGE_PATH) {
                ImageData imageData = (ImageData) entry.getKey();
                ImageGenerator image = new ImageGenerator(imageData.getImagePath(), imageData.getImageHeight(), imageData.getImageChar());
                for (String line : image.getLines()) {
                    tags.add(line);
                }
            }
        }
        return new Hologram(this.locX, this.locY, this.locZ, tags.toArray(new String[tags.size()]));
    }
}