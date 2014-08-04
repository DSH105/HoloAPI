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
import com.dsh105.holoapi.image.ImageGenerator;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * A HologramFactory is responsible for creating an {@link com.dsh105.holoapi.api.Hologram} which can be managed by
 * HoloAPI
 * <p>
 * The HologramFactory implements a fluid hologram builder, allowing parameters to be set as an extension to the
 * constructor
 */

public class HologramFactory extends HoloFactory {

    private ArrayList<String> tags = new ArrayList<>();
    protected HashMap<TagSize, String> imageIdMap = new HashMap<>();

    /**
     * Constructs a HologramFactory
     *
     * @param owningPlugin plugin to register constructed holograms under
     * @throws java.lang.IllegalArgumentException if the owning plugin is null
     */
    public HologramFactory(Plugin owningPlugin) {
        super(owningPlugin);
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
     * Clears existing content (text and images) from the factory
     *
     * @return This object
     */
    public HologramFactory clearContent() {
        this.tags.clear();
        return this;
    }

    /**
     * Adds text to constructed Holograms
     *
     * @param text Text to add to constructed holograms
     * @return This object
     */
    public HologramFactory withText(String... text) {
        Collections.addAll(this.tags, text);
        return this;
    }

    /**
     * Adds an image to constructed Holograms
     * <p>
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
     * @param customImageKey key of the image generator to search for. If a generator is not found, the image will not
     *                       be added
     * @return This object
     */
    public HologramFactory withImage(String customImageKey) {
        ImageGenerator generator = HoloAPI.getImageLoader().getGenerator(customImageKey);
        if (generator != null) {
            this.withImage(generator);
        }
        return this;
    }

    @Override
    public boolean canBuild() {
        return !this.isEmpty();
    }

    @Override
    public Hologram prepareHologram() {
        String[] content = this.tags.toArray(new String[this.tags.size()]);
        Hologram hologram;
        if (this.withTagId) {
            hologram = new HologramImpl(this.tagId, this.saveId, this.worldName, this.locX, this.locY, this.locZ, content);
        } else {
            hologram = new HologramImpl(this.saveId, this.worldName, this.locX, this.locY, this.locZ, content);
        }
        ((HologramImpl) hologram).setImageTagMap(this.imageIdMap);
        return hologram;
    }

    @Override
    public HologramFactory withLocation(Location location) {
        return (HologramFactory) super.withLocation(location);
    }

    @Override
    public HologramFactory withLocation(Vector vectorLocation, String worldName) {
        return (HologramFactory) super.withLocation(vectorLocation, worldName);
    }

    @Override
    public HologramFactory withVisibility(Visibility visibility) {
        return (HologramFactory) super.withVisibility(visibility);
    }

    @Override
    public HologramFactory withSaveId(String saveId) {
        return (HologramFactory) super.withSaveId(saveId);
    }

    @Override
    public HologramFactory withSimplicity(boolean simple) {
        return (HologramFactory) super.withSimplicity(simple);
    }

    @Override
    protected HologramFactory withFirstTagId(int tagId) {
        return (HologramFactory) super.withFirstTagId(tagId);
    }
}