package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
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

    private boolean simple = false;
    private boolean preparedId = false;
    private boolean prepared = false;
    private boolean imageGenerated;

    /**
     * Constructs an AnimatedHologramFactory
     *
     * @param owningPlugin plugin to register constructed holograms under
     */
    public AnimatedHologramFactory(Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new NullPointerException("Plugin cannot be null");
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
        animatedHologram.setSimplicity(this.simple);
        if (!animatedHologram.isSimple()) {
            for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
                if (!h.isSimple()) {
                    h.refreshDisplay();
                }
            }
        }
        for (Entity e : animatedHologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                animatedHologram.show((Player) e);
            }
        }
        HoloAPI.getManager().track(animatedHologram, this.owningPlugin);
        return animatedHologram;
    }
}
