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
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.api.visibility.VisibilityAll;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.util.SaveIdGenerator;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * An AnimatedHologramFactory is responsible for creating an {@link com.dsh105.holoapi.api.AnimatedHologram} which can be managed by HoloAPI
 * <p/>
 * The AnimatedHologramFactory implements a fluid hologram builder, allowing parameters to be set as an extension to the constructor
 */

public class AnimatedHologramFactory {

    private Plugin owningPlugin;

    private AnimatedImageGenerator animatedImage;
    private AnimatedTextGenerator textGenerator;
    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private String saveId;
    private Visibility visibility = new VisibilityAll();

    private boolean simple = false;
    private boolean preparedId = false;
    private boolean prepared = false;
    private boolean imageGenerated;

    /**
     * Constructs an AnimatedHologramFactory
     *
     * @param owningPlugin plugin to register constructed holograms under
     * @throws java.lang.IllegalArgumentException if the owning plugin is null
     */
    public AnimatedHologramFactory(Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.owningPlugin = owningPlugin;
    }

    protected AnimatedHologramFactory withSaveId(String saveId) {
        this.saveId = saveId;
        this.preparedId = true;
        return this;
    }

    private AnimatedHologramFactory withCoords(double x, double y, double z) {
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        this.prepared = true;
        return this;
    }

    private AnimatedHologramFactory withWorld(String worldName) {
        this.worldName = worldName;
        return this;
    }

    /**
     * Sets the location for constructed AnimatedHolograms
     *
     * @param location location for constructed AnimatedHolograms
     * @return This object
     */
    public AnimatedHologramFactory withLocation(Location location) {
        this.withCoords(location.getX(), location.getY(), location.getZ());
        this.withWorld(location.getWorld().getName());
        return this;
    }

    /**
     * Sets the location for constructed AnimatedHolograms
     *
     * @param vectorLocation a {@link org.bukkit.util.Vector} representing the coordinates of constructed AnimatedHolograms
     * @param worldName      the world name to place constructed AnimatedHolograms in
     * @return This object
     */
    public AnimatedHologramFactory withLocation(Vector vectorLocation, String worldName) {
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
    public AnimatedHologramFactory withVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    /**
     * Adds frames of text to constructed AnimatedHolograms
     * <p/>
     *
     * @param textGenerator text generator used to prepare AnimatedHolograms
     * @return This object
     * @see com.dsh105.holoapi.image.AnimatedTextGenerator
     */
    public AnimatedHologramFactory withText(AnimatedTextGenerator textGenerator) {
        this.textGenerator = textGenerator;
        this.imageGenerated = false;
        return this;
    }

    /**
     * Adds image frames to constructed AnimatedHolograms
     * <p/>
     *
     * @param animatedImage animation generator used to prepare AnimatedHolograms
     * @return This object
     * @see com.dsh105.holoapi.image.AnimatedImageGenerator
     */
    public AnimatedHologramFactory withImage(AnimatedImageGenerator animatedImage) {
        this.animatedImage = animatedImage;
        this.imageGenerated = true;
        return this;
    }

    /**
     * /**
     * Adds image frames to constructed AnimatedHolograms
     *
     * @param animatedImageKey key of the animation generator to search for. If a generator is not found, the animation will not be added
     * @return This object
     */
    public AnimatedHologramFactory withImage(String animatedImageKey) {
        AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(animatedImageKey);
        if (generator != null) {
            this.withImage(generator);
        }
        return this;
    }

    /**
     * Sets the simplicity of constructed AnimatedHolograms
     *
     * @param simple simplicity of constructed AnimatedHolograms
     * @return This object
     */
    public AnimatedHologramFactory withSimplicity(boolean simple) {
        this.simple = simple;
        return this;
    }

    /**
     * Constructs an {@link com.dsh105.holoapi.api.AnimatedHologram} based on the settings stored in the factory
     *
     * @return The constructed AnimatedHologram
     * @throws com.dsh105.holoapi.exceptions.HologramNotPreparedException if the animation is empty or the location is not initialised
     */
    public AnimatedHologram build() {
        if ((this.imageGenerated && this.animatedImage == null) || (!this.imageGenerated && this.textGenerator == null)) {
            throw new HologramNotPreparedException("Hologram animation cannot be empty.");
        }
        if (!this.prepared) {
            throw new HologramNotPreparedException("Hologram location cannot be null.");
        }

        if (!this.preparedId || this.saveId == null) {
            this.saveId = SaveIdGenerator.nextId() + "";
        }
        AnimatedHologram animatedHologram;
        if (this.imageGenerated) {
            animatedHologram = new AnimatedHologram(this.saveId, this.worldName, this.locX, this.locY, this.locZ, this.animatedImage);
        } else {
            animatedHologram = new AnimatedHologram(this.saveId, this.worldName, this.locX, this.locY, this.locZ, this.textGenerator);
        }
        animatedHologram.setVisibility(this.visibility);
        animatedHologram.setSimplicity(this.simple);
        if (!animatedHologram.isSimple()) {
            for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
                if (!h.isSimple()) {
                    h.refreshDisplay(true);
                }
            }
        }
        for (Entity e : animatedHologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                animatedHologram.show((Player) e, true);
            }
        }
        HoloAPI.getManager().track(animatedHologram, this.owningPlugin);
        return animatedHologram;
    }
}
