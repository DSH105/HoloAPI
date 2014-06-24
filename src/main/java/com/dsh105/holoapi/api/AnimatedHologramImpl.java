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

public class AnimatedHologramImpl extends HologramImpl implements AnimatedHologram {

    private BukkitTask displayTask;
    private boolean imageGenerated;
    private String animationKey;

    private ArrayList<Frame> frames = new ArrayList<Frame>();
    private int index = 0;
    private Frame currentFrame;

    protected AnimatedHologramImpl(String saveId, String worldName, double x, double y, double z, AnimatedImageGenerator animatedImage) {
        super(TagIdGenerator.nextId(animatedImage.getLargestFrame().getImageGenerator().getLines().length * animatedImage.getFrames().size()),
                saveId, worldName, x, y, z, animatedImage.getLargestFrame().getImageGenerator().getLines());
        this.frames.addAll(animatedImage.getFrames());
        this.currentFrame = this.getCurrent();
        this.animate();
        this.imageGenerated = true;
        this.animationKey = animatedImage.getKey();
    }

    protected AnimatedHologramImpl(String saveId, String worldName, double x, double y, double z, AnimatedTextGenerator textGenerator) {
        super(TagIdGenerator.nextId(textGenerator.getLargestFrame().getLines().length * textGenerator.getFrames().size()),
                saveId, worldName, x, y, z, textGenerator.getLargestFrame().getLines());
        this.frames.addAll(textGenerator.getFrames());
        this.currentFrame = this.getCurrent();
        this.animate();
    }

    @Override
    public boolean isImageGenerated() {
        return this.imageGenerated;
    }

    @Override
    public String getAnimationKey() {
        return this.animationKey;
    }

    @Override
    public Frame getCurrent() {
        return this.getFrame(this.index);
    }

    @Override
    public Frame getNext() {
        return this.getFrame((this.index + 1) >= this.frames.size() ? 0 : this.index + 1);
    }

    @Override
    public Frame next() {
        if (++this.index >= this.frames.size()) {
            this.index = 0;
        }
        return this.getFrame(this.index);
    }

    @Override
    public Frame getFrame(int index) {
        if (index >= this.frames.size()) {
            throw new IndexOutOfBoundsException("Frame " + index + "doesn't exist.");
        }
        return this.frames.get(index);
    }

    @Override
    public ArrayList<Frame> getFrames() {
        ArrayList<Frame> list = new ArrayList<Frame>();
        list.addAll(this.frames);
        return list;
    }

    @Override
    public void updateLine(int index, String content) {
        // Nothing can happen here. Lines are always changing
    }

    @Override
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

    @Override
    public void runAnimation() {
        for (Map.Entry<String, Vector> entry : getPlayerViews().entrySet()) {
            final Player p = PlayerIdent.getPlayerOf(entry.getKey());
            if (p != null) {
                currentFrame = next();
                updateAnimation(p, currentFrame.getLines());
            }
        }
    }

    @Override
    public boolean isAnimating() {
        return this.displayTask != null;
    }

    @Override
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

    @Override
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

    @Override
    public void showAnimation(Player observer, String[] lines) {
        this.showAnimation(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ(), lines);
    }

    @Override
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
