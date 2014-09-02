package com.dsh105.holoapi.api;

import com.dsh105.commodus.IdentUtil;
import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.config.Settings;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.AnimatedTextGenerator;
import com.dsh105.holoapi.image.Frame;
import com.dsh105.holoapi.util.TagIdGenerator;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

public class AnimatedHologramImpl extends HologramImpl implements AnimatedHologram {

    private BukkitTask displayTask;
    private boolean imageGenerated;
    private String animationKey;

    private ArrayList<Frame> frames = new ArrayList<>();
    private int index = 0;
    private Frame currentFrame;

    protected AnimatedHologramImpl(String saveId, String worldName, double x, double y, double z, AnimatedImageGenerator animatedImage) {
        super(TagIdGenerator.next(animatedImage.getLargestFrame().getImageGenerator().getLines().length * animatedImage.getFrames().size()),
                saveId, worldName, x, y, z, animatedImage.getLargestFrame().getImageGenerator().getLines());
        this.frames.addAll(animatedImage.getFrames());
        this.currentFrame = this.getCurrent();
        this.animate();
        this.imageGenerated = true;
        this.animationKey = animatedImage.getKey();
    }

    protected AnimatedHologramImpl(String saveId, String worldName, double x, double y, double z, AnimatedTextGenerator textGenerator) {
        super(TagIdGenerator.next(textGenerator.getLargestFrame().getLines().length * textGenerator.getFrames().size()),
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

    private Frame next() {
        if (++this.index >= this.frames.size()) {
            this.index = 0;
        }
        return this.getFrame(this.index);
    }

    @Override
    public Frame getFrame(int index) {
        if (index >= this.frames.size()) {
            throw new IndexOutOfBoundsException("Frame " + index + "doesn't exist.");
        } else if(index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be less than 0");
        }
        return this.frames.get(index);
    }

    @Override
    public ArrayList<Frame> getFrames() {
        ArrayList<Frame> list = new ArrayList<>();
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

    private void runAnimation() {
        for (Map.Entry<String, Vector> entry : getPlayerViews().entrySet()) {
            final Player p = IdentUtil.getPlayerOf(entry.getKey());
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
    public void updateAnimation(Player observer, String... lines) {
        checkNotNull(observer, "The Player object is null in AnimatedHologramImpl#updateAnimation(...)");
        for (int index = 0; index < lines.length; index++) {
            this.updateNametag(observer, lines[index], index);
        }
    }

    @Override
    public void show(Player observer) {
        checkNotNull(observer, "The Player object is null in AnimatedHologramImpl#show(Player)");
        this.showAnimation(observer, currentFrame.getLines());
    }

    @Override
    public void show(Player observer, Location location) {
        checkNotNull(observer, "The Player object is null in AnimatedHologramImpl#show(Player, Observer)");
        checkNotNull(location, "The Location object is null in AnimatedHologramImpl#show(Player, Observer)");
        this.showAnimation(observer, location.toVector(), currentFrame.getLines());
    }

    @Override
    public void show(Player observer, double x, double y, double z) {
        checkNotNull(observer, "The Player object is null in AnimatedHologramImpl#show(Player, double, double, double)");
        this.showAnimation(observer, x, y, z, currentFrame.getLines());
    }

    @Override
    public void showAnimation(Player observer, String... lines) {
        checkNotNull(observer, "The Player object is null in AnimatedHologramImpl#showAnimation(Player, String...)");
        this.showAnimation(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ(), lines);
    }

    @Override
    public void showAnimation(Player observer, Vector v, String... lines) {
        checkNotNull(observer, "The Player object is null in AnimatedHologramImpl#showAnimation(Player, Vector, String...)");
        checkNotNull(v, "The Vector object is null in AnimatedHologramImpl#showAnimation(Player, Vector, String...)");
        this.showAnimation(observer, v.getBlockX(), v.getBlockY(), v.getBlockZ(), lines);
    }

    private void showAnimation(Player observer, double x, double y, double z, String[] lines) {
        checkNotNull(observer, "The Player object is null in AnimatedHologramImpl#showAnimation(Player, double, double, double, String[])");
        checkNotNull(lines, "The String[] object is null in AnimatedHologramImpl#showAnimation(Player, double, double, double, String[])");
        boolean is1_8 = HoloAPI.getCore().getInjectionManager().is1_8(observer);
        for (int index = 0; index < lines.length; index++) {
            if (is1_8)
                this.generate_1_8(observer, lines[index], index, -index * Settings.VERTICAL_LINE_SPACING.getValue(), x, y, z);
            else
                this.generate_1_7(observer, lines[index], index, -index * Settings.VERTICAL_LINE_SPACING.getValue(), x, y, z);
        }
        this.playerToLocationMap.put(IdentUtil.getIdentificationForAsString(observer), new Vector(x, y, z));
    }

    @Override
    public void move(Player observer, Vector to) {
        checkNotNull(observer, "The Player object is null in AnimatedHologramImpl#move(Player, Vector)");
        checkNotNull(to, "The Vector object is null in AnimatedHologramImpl#move(Player, Vector)");
        this.cancelAnimation();
        super.move(observer, to);
        this.animate();
    }
}
