package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.image.ImageChar;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.ShortIdGenerator;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class HologramFactory {

    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private int id;
    private boolean preparedId = false;
    private boolean prepared = false;

    private ArrayList<String> tags = new ArrayList<String>();

    protected HologramFactory withFirstId(int firstId) {
        this.id = firstId;
        this.preparedId = true;
        return this;
    }

    private HologramFactory withCoords(double x, double y, double z) {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        this.prepared = true;
        return this;
    }

    private HologramFactory withWorld(String worldName) {
        this.worldName = worldName;
        return this;
    }

    public HologramFactory withLocation(Location location) {
        this.withCoords(location.getX(), location.getY(), location.getZ());
        this.withWorld(location.getWorld().getName());
        return this;
    }

    public HologramFactory withLocation(Vector vectorLocation, String worldName) {
        this.withCoords(vectorLocation.getX(), vectorLocation.getY(), vectorLocation.getZ());
        this.withWorld(worldName);
        return this;
    }

    public HologramFactory withText(String... text) {
        for (String tag : text) {
            this.tags.add(tag);
        }
        return this;
    }

    public HologramFactory withImage(ImageGenerator imageGenerator) {
        return this.withText(imageGenerator.getLines());
    }

    public HologramFactory withImage(String imageUrl, int imageHeight) {
        return this.withImage(new ImageGenerator(imageUrl, imageHeight, ImageChar.DARK_SHADE));
    }

    public HologramFactory withImage(String imageUrl, int imageHeight, ImageChar imageChar) {
        return this.withImage(new ImageGenerator(imageUrl, imageHeight, imageChar));
    }

    public Hologram build() {
        if (this.tags.isEmpty()) {
            throw new HologramNotPreparedException("Hologram lines cannot be empty.");
        }
        if (!this.prepared) {
            throw new HologramNotPreparedException("Hologram location cannot be null.");
        }

        String[] lines = this.tags.toArray(new String[this.tags.size()]);
        if (!this.preparedId) {
            this.id = ShortIdGenerator.nextId(lines.length);
        }
        Hologram hologram = new Hologram(this.id, this.worldName, this.locX, this.locY, this.locZ, lines);
        HoloAPI.getManager().track(hologram, HoloAPI.getInstance());
        return hologram;
    }
}