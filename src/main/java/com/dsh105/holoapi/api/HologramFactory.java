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

package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.HoloAPICore;
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.api.visibility.VisibilityAll;
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

/**
 * A HologramFactory is responsible for creating an {@link com.dsh105.holoapi.api.Hologram} which can be managed by HoloAPI
 * <p/>
 * The HologramFactory implements a fluid hologram builder, allowing parameters to be set as an extension to the constructor
 */

public class HologramFactory {

    private Plugin owningPlugin;

    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private String saveId;
    private Visibility visibility = new VisibilityAll();
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
     * @throws java.lang.IllegalArgumentException if the owning plugin is null
     */
    public HologramFactory(Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
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
     * @param worldName      the world name to place constructed Hologram in
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
     * Sets the visibility of constructed Holograms
     *
     * @param visibility visibility of constructed Hologram
     * @return This object
     */
    public HologramFactory withVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    /**
     * Adds text to constructed Holograms
     *
     * @param text Text to add to constructed holograms
     * @return This object
     */
    public HologramFactory withText(String... text) {
        for (String tag : text) {
            this.tags.add(tag);
        }
        return this;
    }

    /**
     * Adds an image to constructed Holograms
     * <p/>
     *
     * @param imageGenerator image generator used to prepare Holograms
     * @return This object
     * @throws java.lang.IllegalArgumentException if the generator is null
     * @see com.dsh105.holoapi.image.ImageGenerator
     */
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
     * @throws com.dsh105.holoapi.exceptions.HologramNotPreparedException if the lines are empty or the location is not initialised
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
            //HoloAPI.getManager().clearFromFile(this.saveId);
            HoloAPICore.LOGGER.log(Level.SEVERE, "Could not find valid world (" + this.worldName + ") for Hologram of ID " + this.saveId + "!");
            WorldListener.store(this.saveId, this.worldName);
            return null;
        }

        Hologram hologram;
        if (this.withTagId) {
            hologram = new Hologram(this.tagId, this.saveId, this.worldName, this.locX, this.locY, this.locZ, lines);
        } else {
            hologram = new Hologram(this.saveId, this.worldName, this.locX, this.locY, this.locZ, lines);
        }
        hologram.setImageTagMap(this.imageIdMap);
        hologram.setVisibility(this.visibility);
        hologram.setSimplicity(this.simple);
        if (!hologram.isSimple()) {
            for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
                if (!h.isSimple()) {
                    h.refreshDisplay(true);
                }
            }
        }
        for (Entity e : hologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                hologram.show((Player) e, true);
            }
        }
        HoloAPI.getManager().track(hologram, this.owningPlugin);
        return hologram;
    }
}
