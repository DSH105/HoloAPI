package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.exceptions.HologramNotPreparedException;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.util.SaveIdGenerator;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class AnimatedHologramFactory {

    private Plugin owningPlugin;

    private AnimatedImageGenerator animatedImage;
    private String worldName;
    private double locX;
    private double locY;
    private double locZ;
    private String saveId;
    private boolean preparedId = false;
    private boolean prepared = false;

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

    public AnimatedHologramFactory withLocation(Location location) {
        this.withCoords(location.getX(), location.getY(), location.getZ());
        this.withWorld(location.getWorld().getName());
        return this;
    }

    public AnimatedHologramFactory withLocation(Vector vectorLocation, String worldName) {
        this.withCoords(vectorLocation.getX(), vectorLocation.getY(), vectorLocation.getZ());
        this.withWorld(worldName);
        return this;
    }

    public AnimatedHologramFactory withImage(AnimatedImageGenerator animatedImage) {
        this.animatedImage = animatedImage;
        return this;
    }

    public AnimatedHologramFactory withImage(String animatedImageKey) {
        AnimatedImageGenerator generator = HoloAPI.getAnimationLoader().getGenerator(animatedImageKey);
        if (generator != null) {
            this.withImage(generator);
        }
        return this;
    }

    public AnimatedHologram build() {
        if (this.animatedImage == null) {
            throw new HologramNotPreparedException("Hologram animated image cannot be empty.");
        }
        if (!this.prepared) {
            throw new HologramNotPreparedException("Hologram location cannot be null.");
        }

        if (!this.preparedId || this.saveId == null) {
            this.saveId = SaveIdGenerator.nextId() + "";
        }
        AnimatedHologram animatedHologram = new AnimatedHologram(this.saveId, this.worldName, this.locX, this.locY, this.locZ, this.animatedImage);
        /*for (Hologram h : HoloAPI.getManager().getAllHolograms().keySet()) {
            h.refreshDisplay();
        }*/
        for (Entity e : animatedHologram.getDefaultLocation().getWorld().getEntities()) {
            if (e instanceof Player) {
                animatedHologram.show((Player) e);
            }
        }
        HoloAPI.getManager().track(animatedHologram, this.owningPlugin);
        return animatedHologram;
    }
}