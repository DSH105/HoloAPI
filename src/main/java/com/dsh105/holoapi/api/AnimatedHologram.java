package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.util.TagIdGenerator;
import com.dsh105.holoapi.util.wrapper.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
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
     * If the hologram is image based, the animation will have an animation key, as defined by the {@link com.dsh105.holoapi.image.AnimatedImageGenerator} used to create it
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
        //final ImageGenerator image = this.currentFrame.getL;
        this.displayTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Vector> entry : getPlayerViews().entrySet()) {
                    final Player p = Bukkit.getPlayerExact(entry.getKey());
                    if (p != null) {
                        //final Vector v = entry.getValue();
                        currentFrame = getNext();
                        updateAnimation(p, currentFrame.getLines());
                        //clear(p);
                        //showAnimation(p, v, currentFrame.getLines());

                    }
                }
                animate();
            }
        }.runTaskLater(HoloAPI.getInstance(), currentFrame.getDelay());
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

    /**
     * Shows the the animation to an observer at a position
     * <p/>
     * Important to note: This method may yield unexpected results if not used properly
     *
     * @param observer Player to show the animation to
     * @param x        x value of the position the animation is to be shown
     * @param y        y value of the position the animation is to be shown
     * @param z        z value of the position the animation is to be shown
     * @param lines    Lines to display
     */
    private void showAnimation(Player observer, double x, double y, double z, String[] lines) {
        for (int index = 0; index < lines.length; index++) {
            this.generateAnimation(observer, lines[index], index, -index * HoloAPI.getHologramLineSpacing(), x, y, z);
        }
        this.playerToLocationMap.put(observer.getName(), new Vector(x, y, z));
    }

    @Override
    public void move(Player observer, Vector to) {
        this.cancelAnimation();
        super.move(observer, to);
        this.animate();
    }

    @Override
    public void clear(Player observer) {
        int[] ids = new int[currentFrame.getLines().length];

        for (int i = 0; i < currentFrame.getLines().length; i++) {
            ids[i] = i;
        }
        clearTags(observer, ids);
        this.playerToLocationMap.remove(observer.getName());
    }

    @Override
    protected void clearTags(Player observer, int... indices) {
        WrapperPacketEntityDestroy destroy = new WrapperPacketEntityDestroy();
        int[] entityIds = new int[indices.length * 2];

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] <= currentFrame.getLines().length) {
                entityIds[i * 2] = this.getHorseIndex(indices[i]);
                entityIds[i * 2 + 1] = this.getSkullIndex(indices[i] * 2);
            }
        }
        destroy.setEntities(entityIds);
        destroy.send(observer);
    }

    @Override
    public void clearAllPlayerViews() {
        Iterator<String> i = this.playerToLocationMap.keySet().iterator();
        while (i.hasNext()) {
            Player p = Bukkit.getPlayerExact(i.next());
            if (p != null) {
                int[] ids = new int[currentFrame.getLines().length];

                for (int j = 0; j < currentFrame.getLines().length; j++) {
                    ids[j] = j;
                }
                clearTags(p, ids);
            }
            i.remove();
        }
    }

    protected void generateAnimation(Player observer, String message, int index, double diffY, double x, double y, double z) {
        WrapperPacketAttachEntity attach = new WrapperPacketAttachEntity();

        WrapperPacketSpawnEntityLiving horse = new WrapperPacketSpawnEntityLiving();
        horse.setEntityId(this.getHorseIndex(index));
        horse.setEntityType(EntityType.HORSE.getTypeId());
        horse.setX(x);
        horse.setY(y + diffY + 55);
        horse.setZ(z);

        message = message.replace("%name%", observer.getName());
        message = message.replace("%balance%", HoloAPI.getInstance().getVaultHook().getBalance(observer));
        message = message.replace("%rank%", HoloAPI.getInstance().getVaultHook().getRank(observer));

        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.watch(10, message);
        dw.watch(11, Byte.valueOf((byte) 1));
        dw.watch(12, Integer.valueOf(-1700000));
        horse.setMetadata(dw);

        WrapperPacketSpawnEntity skull = new WrapperPacketSpawnEntity();
        skull.setEntityId(this.getSkullIndex(index));
        skull.setX(x);
        skull.setY(y + diffY + 55);
        skull.setZ(z);
        skull.setEntityType(66);

        attach.setEntityId(horse.getEntityId());
        attach.setVehicleId(skull.getEntityId());

        horse.send(observer);
        skull.send(observer);
        attach.send(observer);
    }

    @Override
    protected void updateNametag(Player observer, int index) {
        this.updateNametag(observer, this.getCurrent().getLines()[index], index);
    }

    protected void updateNametag(Player observer, String message, int index) {
        message = message.replace("%name%", observer.getName());
        message = message.replace("%balance%", HoloAPI.getInstance().getVaultHook().getBalance(observer));
        message = message.replace("%rank%", HoloAPI.getInstance().getVaultHook().getRank(observer));

        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.watch(10, message);
        dw.watch(11, Byte.valueOf((byte) 1));
        dw.watch(12, Integer.valueOf(-1700000));

        WrapperPacketEntityMetadata metadata = new WrapperPacketEntityMetadata();
        metadata.setEntityId(this.getHorseIndex(index));
        metadata.setMetadata(dw);

        metadata.send(observer);
    }
}
