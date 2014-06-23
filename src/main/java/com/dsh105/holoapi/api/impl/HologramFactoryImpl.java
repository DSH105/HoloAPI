package com.dsh105.holoapi.api.impl;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.HoloAPICore;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.api.visibility.VisibilityDefault;
import com.dsh105.holoapi.api.HologramFactory;
import com.dsh105.holoapi.api.TagSize;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.listeners.WorldListener;
import com.dsh105.holoapi.util.SaveIdGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class HologramFactoryImpl implements HologramFactory {

    private Plugin owningPlugin;

    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private String saveId;
    private Visibility visibility = new VisibilityDefault();
    private boolean simple = false;
    private boolean preparedId = false;
    private boolean prepared = false;

    private ArrayList<String> tags = new ArrayList<String>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<TagSize, String>();
    private int tagId;
    private boolean withTagId;

    public HologramFactoryImpl(Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.owningPlugin = owningPlugin;
    }

    @Override
    public HologramFactory withSaveId(String saveId) {
        this.saveId = saveId;
        this.preparedId = true;
        return this;
    }

    @Override
    public HologramFactory withFirstTagId(int tagId) {
        this.tagId = tagId;
        this.withTagId = true;
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

    @Override
    public HologramFactory withLocation(Location location) {
        this.withCoords(location.getX(), location.getY(), location.getZ());
        this.withWorld(location.getWorld().getName());
        return this;
    }

    @Override
    public HologramFactory withLocation(Vector vectorLocation, String worldName) {
        this.withCoords(vectorLocation.getX(), vectorLocation.getY(), vectorLocation.getZ());
        this.withWorld(worldName);
        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    @Override
    public HologramFactory withVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    @Override
    public HologramFactory withText(String... text) {
        for (String tag : text) {
            this.tags.add(tag);
        }
        return this;
    }

    @Override
    public HologramFactory clearContent() {
        this.tags.clear();
        return this;
    }

    @Override
    public HologramFactory withImage(ImageGenerator imageGenerator) {
        if (imageGenerator == null) {
            throw new IllegalArgumentException("Image generator cannot be null");
        }
        int first = this.tags.size() - 1;
        if (first < 0) first = 0;
        int length = imageGenerator.getLines().length - 1;
        if (imageGenerator.getKey() != null) {
            this.imageIdMap.put(new TagSize(first, first + length), imageGenerator.getKey());
        }
        return this.withText(imageGenerator.getLines());
    }

    @Override
    public HologramFactory withImage(String customImageKey) {
        ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(customImageKey);
        if (generator != null) {
            this.withImage(generator);
        }
        return this;
    }

    @Override
    public HologramFactory withSimplicity(boolean simple) {
        this.simple = simple;
        return this;
    }

    @Override
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
            //HoloAPI.getManager().clearFromFile(this.saveId);
            HoloAPICore.LOGGER.log(Level.SEVERE, "Could not find valid world (" + this.worldName + ") for Hologram of ID " + this.saveId + "!");
            WorldListener.store(this.saveId, this.worldName);
            return null;
        }

        Hologram hologram;
        if (this.withTagId) {
            hologram = new HologramImpl(this.tagId, this.saveId, this.worldName, this.locX, this.locY, this.locZ, lines);
        } else {
            hologram = new HologramImpl(this.saveId, this.worldName, this.locX, this.locY, this.locZ, lines);
        }
        hologram.setImageTagMap(this.imageIdMap);
        hologram.setSimplicity(this.simple);
        hologram.setVisibility(this.visibility);
        /*if (!hologram.isSimple()) {
            for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
                if (!h.isSimple()) {
                    h.refreshDisplay(true);
                }
            }
        }*/
        for (Entity e : hologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                hologram.show((Player) e, true);
            }
        }
        HoloAPI.getManager().track(hologram, this.owningPlugin);
        return hologram;
    }
}
