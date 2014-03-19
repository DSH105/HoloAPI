package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.SaveIdGenerator;
import com.dsh105.holoapi.util.UnicodeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.dsh105.holoapi.util.wrapper.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * A HologramFactory is responsible for creating an {@link com.dsh105.holoapi.api.Hologram} which can be managed by HoloAPI
 * <p>
 * The HologramFactory implements a fluid hologram builder, allowing parameters to be set as an extension to the constructor
 */

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

    /**
     * Constructs a HologramFactory
     *
     * @param owningPlugin plugin to register constructed holograms under
     */
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

    /**
     * Sets the location for constructed Holograms
     *
     * @param location location for constructed Holograms
     * @return This object
     */
    public HologramFactory withLocation(Location location) {
        this.withCoords(location.getX(), location.getY(), location.getZ());
        this.withWorld(location.getWorld().getName());
        return this;
    }

    /**
     * Sets the location for constructed Holograms
     *
     * @param vectorLocation a {@link org.bukkit.util.Vector} representing the coordinates of constructed Holograms
     * @param worldName the world name to place constructed Hologram in
     * @return This object
     */
    public HologramFactory withLocation(Vector vectorLocation, String worldName) {
        this.withCoords(vectorLocation.getX(), vectorLocation.getY(), vectorLocation.getZ());
        this.withWorld(worldName);
        return this;
    }

    /**
     * Gets the emptiness state of the stored lines for constructed Holograms
     *
     * @return true if no tags exist
     */
    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    /**
     * Adds text to constructed Holograms
     *
     * @param text
     * @returnThis object
     */
    public HologramFactory withText(String... text) {
        for (String tag : text) {
            this.tags.add(UnicodeFormatter.replaceAll(tag));
        }
        return this;
    }

    /**
     * Adds an image to constructed Holograms
     * <p>
     * @see com.dsh105.holoapi.image.ImageGenerator
     *
     * @param imageGenerator image generator used to prepare Holograms
     * @return This object
     */
    public HologramFactory withImage(ImageGenerator imageGenerator) {
        int first = this.tags.size() - 1;
        if (first < 0) first = 0;
        int last = imageGenerator.getLines().length - 1;
        if (imageGenerator.getKey() != null) {
            this.imageIdMap.put(new TagSize(first, last), imageGenerator.getKey());
        }
        return this.withText(imageGenerator.getLines());
    }

    /**
     * Adds image frames to constructed AnimatedHolograms
     *
     * @param customImageKey key of the image generator to search for. If a generator is not found, the image will not be added
     * @return This object
     */
    public HologramFactory withImage(String customImageKey) {
        ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(customImageKey);
        if (generator != null) {
            this.withImage(generator);
        }
        return this;
    }

    /**
     * Sets the simplicity of constructed Holograms
     *
     * @param simple simplicity of constructed Holograms
     * @return This object
     */
    public HologramFactory withSimplicity(boolean simple) {
        this.simple = simple;
        return this;
    }

    protected HologramFactory withFirstTagId(int tagId) {
        this.tagId = tagId;
        this.withTagId = true;
        return this;
    }

    /**
     * Constructs an {@link com.dsh105.holoapi.api.Hologram} based on the settings stored in the factory
     *
     * @return The constructed Hologram
     */
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
        hologram.setImageTagMap(this.imageIdMap);
        hologram.setSimplicity(this.simple);
        if (!hologram.isSimple()) {
            for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
                if (!h.isSimple()) {
                    h.refreshDisplay();
                }
            }
        }
        for (Entity e : hologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                hologram.show((Player) e);
            }
        }
        HoloAPI.getManager().track(hologram, this.owningPlugin);
        return hologram;
    }
}
