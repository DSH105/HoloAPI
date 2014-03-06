package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.SaveIdGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HologramFactory {

    private Plugin owningPlugin;

    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private String saveId;
    private boolean simple = false;
    private boolean preparedId = false;
    private boolean prepared = false;

    private ArrayList<String> tags = new ArrayList<String>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<TagSize, String>();
    private int tagId;
    private boolean withTagId;

    public HologramFactory(Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new NullPointerException("Plugin cannot be null");
        }
        this.owningPlugin = owningPlugin;
    }

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

    protected HologramFactory isSimple(boolean simple) {
        this.simple = simple;
        return this;
    }

    protected HologramFactory withFirstTagId(int tagId) {
        this.tagId = tagId;
        this.withTagId = true;
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

        if (Bukkit.getWorld(this.worldName) == null) {
            HoloAPI.getManager().clearFromFile(this.saveId);
            HoloAPI.LOGGER.log(Level.SEVERE, "Could not find valid world (" + this.worldName + ") for Hologram of ID " + this.saveId + "!");
            return null;
        }

        Hologram hologram;
        if (this.withTagId) {
            hologram = new Hologram(this.tagId, this.saveId, this.worldName, this.locX, this.locY, this.locZ, lines);
        } else {
            hologram = new Hologram(this.saveId, this.worldName, this.locX, this.locY, this.locZ, lines);
        }
        /*for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
            h.refreshDisplay();
        }*/
        for (Entity e : hologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                hologram.show((Player) e);
            }
        }
        hologram.setImageTagMap(this.imageIdMap);
        hologram.setSimple(this.simple);
        HoloAPI.getManager().track(hologram, this.owningPlugin);
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