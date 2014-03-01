package com.dsh105.holoapi.api;

import com.dsh105.dshutils.util.GeometryUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.exceptions.ImageNotLoadedException;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.SaveIdGenerator;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HologramFactory {

    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private String saveId;
    private boolean preparedId = false;
    private boolean prepared = false;

    private ArrayList<String> tags = new ArrayList<String>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<TagSize, String>();

    protected HologramFactory withSaveId(String saveId) {
        this.saveId = saveId;
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

    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    public HologramFactory withText(String... text) {
        for (String tag : text) {
            this.tags.add(tag);
        }
        return this;
    }

    public HologramFactory withImage(ImageGenerator imageGenerator) {
        int first = this.tags.size() - 1;
        if (first < 0) first = 0;
        int last = imageGenerator.getLines().length - 1;
        this.imageIdMap.put(new TagSize(first, last), imageGenerator.getKey());
        return this.withText(imageGenerator.getLines());
    }

    public HologramFactory withImage(String customImageKey) {
        ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(customImageKey);
        if (generator != null) {
            this.withImage(generator);
        }
        return this;
    }

    public Hologram build() {
        if (this.isEmpty()) {
            throw new HologramNotPreparedException("Hologram lines cannot be empty.");
        }
        if (!this.prepared) {
            throw new HologramNotPreparedException("Hologram location cannot be null.");
        }

        String[] lines = this.tags.toArray(new String[this.tags.size()]);
        if (!this.preparedId || this.saveId == null) {
            //Map.Entry<TagSize, String> imageIndex = getImageIdOfIndex(0);
            this.saveId = SaveIdGenerator.nextId() + "";
        }
        Hologram hologram = new Hologram(this.saveId, this.worldName, this.locX, this.locY, this.locZ, lines);
        for (Entity e : GeometryUtil.getNearbyEntities(hologram.getDefaultLocation(), 50)) {
            if (e instanceof Player) {
                hologram.show((Player) e);
            }
        }
        hologram.setImageTagMap(this.imageIdMap);
        HoloAPI.getManager().track(hologram, HoloAPI.getInstance());
        return hologram;
    }

    protected Map.Entry<TagSize, String> getImageIdOfIndex(int index) {
        for (Map.Entry<TagSize, String> entry : this.imageIdMap.entrySet()) {
            if (entry.getKey().getFirst() == index) {
                return entry;
            }
        }
        return null;
    }
}