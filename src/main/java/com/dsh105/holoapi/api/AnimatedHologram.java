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
import java.util.logging.Level;

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

    public boolean isImageGenerated() {
        return imageGenerated;
    }

    public String getAnimationKey() {
        return animationKey;
    }

    public Frame getCurrent() {
        return this.getFrame(this.index);
    }

    public Frame getNext() {
        if (++this.index >= this.frames.size()) {
            this.index = 0;
        }
        return this.getFrame(this.index);
    }

    public Frame getFrame(int index) {
        if (index >= this.frames.size()) {
            return null;
        }
        return this.frames.get(index);
    }

    public ArrayList<Frame> getFrames() {
        ArrayList<Frame> list = new ArrayList<Frame>();
        list.addAll(this.frames);
        return list;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    protected void setSimple(boolean flag) {
        // Do nothing. Animated holograms can't be simple
    }

    @Override
    public void updateLine(int index, String content) {
        // Nothing can happen here. Lines are always changing
    }

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

    public boolean isAnimating() {
        return this.displayTask != null;
    }

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

    public void showAnimation(Player observer, String[] lines) {
        this.showAnimation(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ(), lines);
    }

    public void showAnimation(Player observer, Vector v, String[] lines) {
        this.showAnimation(observer, v.getBlockX(), v.getBlockY(), v.getBlockZ(), lines);
    }

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

        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.watch(10, message.replace("%name%", observer.getName()));
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
        WrappedDataWatcher dw = new WrappedDataWatcher();
        dw.watch(10, message.replace("%name%", observer.getName()));
        dw.watch(11, Byte.valueOf((byte) 1));
        dw.watch(12, Integer.valueOf(-1700000));

        WrapperPacketEntityMetadata metadata = new WrapperPacketEntityMetadata();
        metadata.setEntityId(this.getHorseIndex(index));
        metadata.setMetadata(dw);

        metadata.send(observer);
    }
}