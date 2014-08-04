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
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * An AnimatedHologramFactory is responsible for creating an {@link com.dsh105.holoapi.api.AnimatedHologram} which can
 * be managed by HoloAPI
 * <p>
 * The AnimatedHologramFactory implements a fluid hologram builder, allowing parameters to be set as an extension to
 * the
 * constructor
 */

public class AnimatedHologramFactory extends HoloFactory {

    private AnimatedImageGenerator animatedImage;
    private AnimatedTextGenerator textGenerator;
    private boolean imageGenerated;

    /**
     * Constructs an AnimatedHologramFactory
     *
     * @param owningPlugin plugin to register constructed holograms under
     * @throws java.lang.IllegalArgumentException if the owning plugin is null
     */
    public AnimatedHologramFactory(Plugin owningPlugin) {
        super(owningPlugin);
    }

    /**
     * Adds frames of text to constructed AnimatedHolograms
     * <p>
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
     * <p>
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
     * 
     * Adds image frames to constructed AnimatedHolograms
     *
     * @param animatedImageKey key of the animation generator to search for. If a generator is not found, the animation
     *                         will not be added
     * @return This object
     */
    public AnimatedHologramFactory withImage(String animatedImageKey) {
        AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(animatedImageKey);
        if (generator != null) {
            this.withImage(generator);
        }
        return this;
    }

    @Override
    public boolean canBuild() {
        return this.imageGenerated ? this.animatedImage == null : this.textGenerator == null;
    }

    @Override
    public AnimatedHologram prepareHologram() {
        if (this.imageGenerated) {
            return new AnimatedHologramImpl(this.saveId, this.worldName, this.locX, this.locY, this.locZ, this.animatedImage);
        }
        return new AnimatedHologramImpl(this.saveId, this.worldName, this.locX, this.locY, this.locZ, this.textGenerator);
    }

    @Override
    public AnimatedHologramFactory withLocation(Location location) {
        return (AnimatedHologramFactory) super.withLocation(location);
    }

    @Override
    public AnimatedHologramFactory withLocation(Vector vectorLocation, String worldName) {
        return (AnimatedHologramFactory) super.withLocation(vectorLocation, worldName);
    }

    @Override
    public AnimatedHologramFactory withVisibility(Visibility visibility) {
        return (AnimatedHologramFactory) super.withVisibility(visibility);
    }

    @Override
    public AnimatedHologramFactory withSaveId(String saveId) {
        return (AnimatedHologramFactory) super.withSaveId(saveId);
    }

    @Override
    public AnimatedHologramFactory withSimplicity(boolean simple) {
        return (AnimatedHologramFactory) super.withSimplicity(simple);
    }

    @Override
    public AnimatedHologram build() {
        return (AnimatedHologram) super.build();
    }

    @Override
    protected AnimatedHologramFactory withFirstTagId(int tagId) {
        return (AnimatedHologramFactory) super.withFirstTagId(tagId);
    }
}