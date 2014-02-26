package com.dsh105.holoapi.api;

import com.dsh105.dshutils.util.GeometryUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.exceptions.ImageNotLoadedException;
import com.dsh105.holoapi.image.ImageChar;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.ShortIdGenerator;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class HologramFactory {

    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private int id;
    private boolean preparedId = false;
    private boolean prepared = false;

    private ArrayList<String> tags = new ArrayList<String>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<TagSize, String>();

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
        int first = this.tags.size() - 1;
        int last = imageGenerator.getLines().length - 1;
        this.imageIdMap.put(new TagSize(first, last), imageGenerator.getImageKey());
        return this.withText(imageGenerator.getLines());
    }

    public HologramFactory withImage(String customImageKey) {
        ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(customImageKey);
        if (generator != null) {
            int first = this.tags.size() - 1;
            int last = generator.getLines().length - 1;
            this.imageIdMap.put(new TagSize(first, last), generator.getImageKey());
            this.withText(generator.getLines());
        } else {
            throw new ImageNotLoadedException(customImageKey);
        }
        return this;
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
        for (Entity e : GeometryUtil.getNearbyEntities(hologram.getDefaultLocation(), 50)) {
            if (e instanceof Player) {
                hologram.show((Player) e);
            }
        }
        hologram.setImageTagMap(this.imageIdMap);
        HoloAPI.getManager().track(hologram, HoloAPI.getInstance());
        return hologram;
    }
}