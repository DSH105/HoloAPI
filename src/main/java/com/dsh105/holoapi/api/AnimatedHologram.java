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
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.util.PlayerIdent;
import com.dsh105.holoapi.util.TagIdGenerator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Map;

/**
 * Represents an AnimatedHologram that consists of either image or text frames displayed in a constant sequence
 */

public class AnimatedHologram extends Hologram {

    private BukkitTask displayTask;
    private boolean imageGenerated;
    private String animationKey;

    private ArrayList<Frame> frames = new ArrayList<Frame>();
    private int index = 0;
    private Frame currentFrame;

    protected AnimatedHologram(String saveId, String worldName, double x, double y, double z, AnimatedImageGenerator animatedImage) {
        super(TagIdGenerator.nextId(animatedImage.getLargestFrame().getImageGenerator().getLines().length * animatedImage.getFrames().size()),
                saveId, worldName, x, y, z, animatedImage.getLargestFrame().getImageGenerator().getLines());
        this.frames.addAll(animatedImage.getFrames());
        this.currentFrame = this.getCurrent();
        this.animate();
        this.imageGenerated = true;
        this.animationKey = animatedImage.getKey();
    }

    protected AnimatedHologram(String saveId, String worldName, double x, double y, double z, AnimatedTextGenerator textGenerator) {
        super(TagIdGenerator.nextId(textGenerator.getLargestFrame().getLines().length * textGenerator.getFrames().size()),
                saveId, worldName, x, y, z, textGenerator.getLargestFrame().getLines());
        this.frames.addAll(textGenerator.getFrames());
        this.currentFrame = this.getCurrent();
        this.animate();
    }

    /**
     * Gets whether this hologram is image or text based
     *
     * @return true if the hologram is image based, false if text based
     */
    public boolean isImageGenerated() {
        return imageGenerated;
    }

    /**
     * Gets the animation key of the hologram
     * <p/>
     * If the hologram is image based, the animation will have an animation key, as defined by the {@link
     * com.dsh105.holoapi.image.AnimatedImageGenerator} used to create it
     *
     * @return key of the animation used to create the hologram. Returns null if the hologram is text-based
     */
    public String getAnimationKey() {
        return animationKey;
    }

    /**
     * Gets the current frame of the animated hologram
     *
     * @return the current frame of the animated hologram
     */
    public Frame getCurrent() {
        return this.getFrame(this.index);
    }

    /**
     * Gets the next frame of the animated hologram
     *
     * @return the next frame of the animated hologram
     */
    public Frame getNext() {
        return this.getFrame((this.index + 1) >= this.frames.size() ? 0 : this.index + 1);
    }

    private Frame next() {
        if (++this.index >= this.frames.size()) {
            this.index = 0;
        }
        return this.getFrame(this.index);
    }

    /**
     * Gets the frame by its index
     *
     * @param index index of the frame to get
     * @return frame of the animated hologram by its index
     * @throws java.lang.IndexOutOfBoundsException if the frame of the specified index doesn't exist
     */
    public Frame getFrame(int index) {
        if (index >= this.frames.size()) {
            throw new IndexOutOfBoundsException("Frame " + index + "doesn't exist.");
        }
        return this.frames.get(index);
    }

    /**
     * Gets all frames of the animated hologram
     *
     * @return frames of the animated hologram
     */
    public ArrayList<Frame> getFrames() {
        ArrayList<Frame> list = new ArrayList<Frame>();
        list.addAll(this.frames);
        return list;
    }

    @Override
    public void updateLine(int index, String content) {
        // Nothing can happen here. Lines are always changing
    }

    /**
     * Begin the animation of the hologram
     */
    public void animate() {
        if (this.isAnimating()) {
            this.cancelAnimation();
        }
        this.displayTask = new BukkitRunnable() {
            @Override
            public void run() {
                runAnimation();
            }
        }.runTaskTimer(HoloAPI.getCore(), 0L, currentFrame.getDelay());
    }

    private void runAnimation() {
        for (Map.Entry<String, Vector> entry : getPlayerViews().entrySet()) {
            final Player p = PlayerIdent.getPlayerOf(entry.getKey());
            if (p != null) {
                currentFrame = next();
                updateAnimation(p, currentFrame.getLines());
            }
        }
    }

    /**
     * Gets whether the animated hologram has an active animation
     *
     * @return true if the hologram is currently animating
     */
    public boolean isAnimating() {
        return this.displayTask != null;
    }

    /**
     * Cancels the current animation
     */
    public void cancelAnimation() {
        if (this.displayTask != null) {
            this.displayTask.cancel();
            this.displayTask = null;
        }
    }

    @Override
    public void refreshDisplay() {
        this.cancelAnimation();
        this.animate();
    }

    /**
     * Updates the animation for an observer
     * <p/>
     * Important to note: This method may yield unexpected results if not used properly
     *
     * @param observer Player to update the animation for
     * @param lines    Lines to display
     */
    public void updateAnimation(Player observer, String[] lines) {
        for (int index = 0; index < lines.length; index++) {
            this.updateNametag(observer, lines[index], index);
        }
    }

    @Override
    public void show(Player observer) {
        this.showAnimation(observer, currentFrame.getLines());
    }

    @Override
    public void show(Player observer, Location location) {
        this.showAnimation(observer, location.toVector(), currentFrame.getLines());
    }

    @Override
    public void show(Player observer, double x, double y, double z) {
        this.showAnimation(observer, x, y, z, currentFrame.getLines());
    }

    /**
     * Shows the the animation to an observer
     * <p/>
     * Important to note: This method may yield unexpected results if not used properly
     *
     * @param observer Player to show the animation to
     * @param lines    Lines to display
     */
    public void showAnimation(Player observer, String[] lines) {
        this.showAnimation(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ(), lines);
    }

    /**
     * Shows the the animation to an observer at a position
     * <p/>
     * Important to note: This method may yield unexpected results if not used properly
     *
     * @param observer Player to show the animation to
     * @param v        Position to show the animation at
     * @param lines    Lines to display
     */
    public void showAnimation(Player observer, Vector v, String[] lines) {
        this.showAnimation(observer, v.getBlockX(), v.getBlockY(), v.getBlockZ(), lines);
    }

    private void showAnimation(Player observer, double x, double y, double z, String[] lines) {
        for (int index = 0; index < lines.length; index++) {
            this.generate(observer, lines[index], index, -index * HoloAPI.getHologramLineSpacing(), x, y, z);
        }
        this.playerToLocationMap.put(PlayerIdent.getIdentificationForAsString(observer), new Vector(x, y, z));
    }

    @Override
    public void move(Player observer, Vector to) {
        this.cancelAnimation();
        super.move(observer, to);
        this.animate();
    }
}
