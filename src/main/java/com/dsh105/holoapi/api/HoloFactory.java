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
import com.dsh105.holoapi.api.visibility.VisibilityDefault;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.listeners.WorldListener;
import com.dsh105.holoapi.util.SaveIdGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public abstract class HoloFactory {

    private Plugin owningPlugin;

    protected String worldName;
    protected double locX;
    protected double locY;
    protected double locZ;
    protected String saveId;
    protected Visibility visibility = new VisibilityDefault();
    protected boolean simple = false;
    protected int tagId;
    protected boolean withTagId;

    private boolean prepared = false;
    private boolean preparedId = false;

    /**
     * Constructs a factory for building a hologram
     *
     * @param owningPlugin plugin to register constructed hologram under
     * @throws java.lang.IllegalArgumentException if the owning plugin is null
     */
    public HoloFactory(Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.owningPlugin = owningPlugin;
    }

    /**
     * Sets the location for constructed Holograms
     *
     * @param location location for constructed Holograms
     * @return This object
     */
    public HoloFactory withLocation(Location location) {
        this.withCoords(location.getX(), location.getY(), location.getZ());
        this.withWorld(location.getWorld().getName());
        return this;
    }

    /**
     * Sets the location for constructed Holograms
     *
     * @param vectorLocation a {@link org.bukkit.util.Vector} representing the coordinates of constructed
     *                       Holograms
     * @param worldName      the world name to place constructed Holograms in
     * @return This object
     */
    public HoloFactory withLocation(Vector vectorLocation, String worldName) {
        this.withCoords(vectorLocation.getX(), vectorLocation.getY(), vectorLocation.getZ());
        this.withWorld(worldName);
        return this;
    }

    /**
     * Sets the visibility of constructed Holograms
     *
     * @param visibility visibility of constructed Hologram
     * @return This object
     */
    public HoloFactory withVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    /**
     * Sets the save id of constructed Holograms
     *
     * @param saveId save id to be assigned to constructed Holograms
     * @throws com.dsh105.holoapi.exceptions.DuplicateSaveIdException if the save ID is already registered
     */
    public HoloFactory withSaveId(String saveId) {
        this.saveId = saveId;
        this.preparedId = true;
        return this;
    }

    /**
     * Sets the simplicity of constructed Holograms
     *
     * @param simple simplicity of constructed Holograms
     * @return This object
     */
    public HoloFactory withSimplicity(boolean simple) {
        this.simple = simple;
        return this;
    }

    public abstract boolean canBuild();

    public abstract Hologram prepareHologram();

    /**
     * Constructs a {@link com.dsh105.holoapi.api.Hologram} based on the settings stored in the factory
     *
     * @return The constructed Holograms
     * @throws com.dsh105.holoapi.exceptions.HologramNotPreparedException if the animation is empty or the location is
     *                                                                    not initialised
     */
    public Hologram build() {
        if (!this.canBuild() || !this.prepared) {
            throw new HologramNotPreparedException("Hologram is not prepared correctly!");
        }

        if (!this.preparedId || this.saveId == null) {
            this.saveId = SaveIdGenerator.nextId() + "";
        }

        if (Bukkit.getWorld(this.worldName) == null) {
            HoloAPICore.LOGGER.log(Level.WARNING, "Could not find valid world (" + this.worldName + ") for Hologram of ID " + this.saveId + ". Maybe the world isn't loaded yet?");
            WorldListener.store(this.saveId, this.worldName);
            return null;
        }

        Hologram hologram = prepareHologram();
        hologram.setVisibility(this.visibility);
        hologram.setSimplicity(this.simple);
        hologram.showNearby();
        HoloAPI.getManager().track(hologram, this.owningPlugin);
        return hologram;
    }

    protected HoloFactory withFirstTagId(int tagId) {
        this.tagId = tagId;
        this.withTagId = true;
        return this;
    }

    private HoloFactory withCoords(double x, double y, double z) {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        this.prepared = true;
        return this;
    }

    private HoloFactory withWorld(String worldName) {
        this.worldName = worldName;
        return this;
    }
}