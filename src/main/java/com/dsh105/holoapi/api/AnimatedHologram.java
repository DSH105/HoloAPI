package com.dsh105.holoapi.api;

import com.dsh105.holoapi.image.Frame;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public interface AnimatedHologram extends Hologram {

    /**
     * Gets whether this hologram is image or text based
     *
     * @return true if the hologram is image based, false if text based
     */
    public boolean isImageGenerated();

    /**
     * Gets the animation key of the hologram
     * <p/>
     * If the hologram is image based, the animation will have an animation key, as defined by the {@link
     * com.dsh105.holoapi.image.AnimatedImageGenerator} used to create it
     *
     * @return key of the animation used to create the hologram. Returns null if the hologram is text-based
     */
    public String getAnimationKey();

    /**
     * Gets the current frame of the animated hologram
     *
     * @return the current frame of the animated hologram
     */
    public Frame getCurrent();

    /**
     * Gets the next frame of the animated hologram
     *
     * @return the next frame of the animated hologram
     */
    public Frame getNext();

    public Frame next();

    /**
     * Gets the frame by its index
     *
     * @param index index of the frame to get
     * @return frame of the animated hologram by its index
     * @throws java.lang.IndexOutOfBoundsException if the frame of the specified index doesn't exist
     */
    public Frame getFrame(int index);

    /**
     * Gets all frames of the animated hologram
     *
     * @return frames of the animated hologram
     */
    public ArrayList<Frame> getFrames();

    @Override
    public void updateLine(int index, String content);

    /**
     * Begin the animation of the hologram
     */
    public void animate();

    public void runAnimation();

    /**
     * Gets whether the animated hologram has an active animation
     *
     * @return true if the hologram is currently animating
     */
    public boolean isAnimating();

    /**
     * Cancels the current animation
     */
    public void cancelAnimation();

    @Override
    public void refreshDisplay();

    /**
     * Updates the animation for an observer
     * <p/>
     * Important to note: This method may yield unexpected results if not used properly
     *
     * @param observer Player to update the animation for
     * @param lines    Lines to display
     */
    public void updateAnimation(Player observer, String[] lines);

    @Override
    public void show(Player observer);

    @Override
    public void show(Player observer, Location location);

    @Override
    public void show(Player observer, double x, double y, double z);

    /**
     * Shows the the animation to an observer
     * <p/>
     * Important to note: This method may yield unexpected results if not used properly
     *
     * @param observer Player to show the animation to
     * @param lines    Lines to display
     */
    public void showAnimation(Player observer, String[] lines);

    /**
     * Shows the the animation to an observer at a position
     * <p/>
     * Important to note: This method may yield unexpected results if not used properly
     *
     * @param observer Player to show the animation to
     * @param v        Position to show the animation at
     * @param lines    Lines to display
     */
    public void showAnimation(Player observer, Vector v, String[] lines);
}
