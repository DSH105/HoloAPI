package com.dsh105.holoapi.api.impl;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.HoloAPICore;
import com.dsh105.holoapi.api.AnimatedHologram;
import com.dsh105.holoapi.api.visibility.VisibilityDefault;
import com.dsh105.holoapi.api.AnimatedHologramFactory;
import com.dsh105.holoapi.api.visibility.Visibility;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.listeners.WorldListener;
import com.dsh105.holoapi.util.SaveIdGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class AnimatedHologramFactoryImpl implements AnimatedHologramFactory {

    private Plugin owningPlugin;

    private AnimatedImageGenerator animatedImage;
    private AnimatedTextGenerator textGenerator;
    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private String saveId;
    private Visibility visibility = new VisibilityDefault();

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
    public AnimatedHologramFactoryImpl(Plugin owningPlugin) {
        if (owningPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.owningPlugin = owningPlugin;
    }

    public AnimatedHologramFactory withSaveId(String saveId) {
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

    @Override
    public AnimatedHologramFactory withLocation(Location location) {
        this.withCoords(location.getX(), location.getY(), location.getZ());
        this.withWorld(location.getWorld().getName());
        return this;
    }

    @Override
    public AnimatedHologramFactory withLocation(Vector vectorLocation, String worldName) {
        this.withCoords(vectorLocation.getX(), vectorLocation.getY(), vectorLocation.getZ());
        this.withWorld(worldName);
        return this;
    }

    @Override
    public AnimatedHologramFactory withVisibility(Visibility visibility) {
        this.visibility = visibility;
        return this;
    }

    @Override
    public AnimatedHologramFactory withText(AnimatedTextGenerator textGenerator) {
        this.textGenerator = textGenerator;
        this.imageGenerated = false;
        return this;
    }

    @Override
    public AnimatedHologramFactory withImage(AnimatedImageGenerator animatedImage) {
        this.animatedImage = animatedImage;
        this.imageGenerated = true;
        return this;
    }

    @Override
    public AnimatedHologramFactory withImage(String animatedImageKey) {
        AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(animatedImageKey);
        if (generator != null) {
            this.withImage(generator);
        }
        return this;
    }

    @Override
    public AnimatedHologramFactory withSimplicity(boolean simple) {
        this.simple = simple;
        return this;
    }

    @Override
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

        if (Bukkit.getWorld(this.worldName) == null) {
            //HoloAPI.getManager().clearFromFile(this.saveId);
            HoloAPICore.LOGGER.log(Level.SEVERE, "Could not find valid world (" + this.worldName + ") for Hologram of ID " + this.saveId + "!");
            WorldListener.store(this.saveId, this.worldName);
            return null;
        }

        if (this.imageGenerated) {
            animatedHologram = new AnimatedHologramImpl(this.saveId, this.worldName, this.locX, this.locY, this.locZ, this.animatedImage);
        } else {
            animatedHologram = new AnimatedHologramImpl(this.saveId, this.worldName, this.locX, this.locY, this.locZ, this.textGenerator);
        }
        animatedHologram.setVisibility(this.visibility);
        animatedHologram.setSimplicity(this.simple);
        /*if (!animatedHologram.isSimple()) {
            for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
                if (!h.isSimple()) {
                    h.refreshDisplay(true);
                }
            }
        }*/
        for (Entity e : animatedHologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                animatedHologram.show((Player) e, true);
            }
        }
        HoloAPI.getManager().track(animatedHologram, this.owningPlugin);
        return animatedHologram;
    }
}
