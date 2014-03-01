package com.dsh105.holoapi.api;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.image.AnimatedImageGenerator;
import com.dsh105.holoapi.image.GIFFrame;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.wrapper.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.Map;

public class AnimatedHologram extends Hologram {

    private BukkitTask displayTask;
    private final AnimatedImageGenerator animatedImage;
    private GIFFrame frame;

    protected AnimatedHologram(String saveId, String worldName, double x, double y, double z, AnimatedImageGenerator animatedImage) {
        super(saveId, worldName, x, y, z, animatedImage.getFrame(0).getImageGenerator().getLines());
        this.animatedImage = animatedImage;
        this.frame = this.animatedImage.getCurrent();
        this.animate();
    }

    public void animate() {
        final ImageGenerator image = this.frame.getImageGenerator();
        this.displayTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<String, Vector> entry : getPlayerViews().entrySet()) {
                    Player p = Bukkit.getPlayerExact(entry.getKey());
                    if (p != null) {
                        Vector v = entry.getValue();
                        clear(p);
                        showAnimation(p, v, image);
                        frame = animatedImage.getNext();
                        animate();
                    }
                }
            }
        }.runTaskLater(HoloAPI.getInstance(), (int) Math.ceil(frame.getDelay() / 2.5));
    }

    public boolean isAnimating() {
        return this.displayTask != null ;
    }

    public void cancelAnimation() {
        if (this.displayTask != null) {
            this.displayTask.cancel();
            this.displayTask = null;
        }
    }

    public AnimatedImageGenerator getAnimatedImage() {
        return animatedImage;
    }

    @Override
    public void show(Player observer) {
        this.showAnimation(observer, frame.getImageGenerator());
    }

    @Override
    public void show(Player observer, Location location) {
        this.showAnimation(observer, location.toVector(), frame.getImageGenerator());
    }

    @Override
    public void show(Player observer, double x, double y, double z) {
        this.showAnimation(observer, x, y, z, frame.getImageGenerator());
    }

    public void showAnimation(Player observer, ImageGenerator generator) {
        this.showAnimation(observer, this.getDefaultX(), this.getDefaultY(), this.getDefaultZ(), generator);
    }

    public void showAnimation(Player observer, Vector v, ImageGenerator generator) {
        this.showAnimation(observer, v.getBlockX(), v.getBlockY(), v.getBlockZ(), generator);
    }

    private void showAnimation(Player observer, double x, double y, double z, ImageGenerator generator) {
        for (int index = 0; index < generator.getLines().length; index++) {
            this.generateAnimation(observer, generator.getLines()[index], index, -index * HoloAPI.getHologramLineSpacing(), x, y, z);
        }
        this.playerToLocationMap.put(observer.getName(), new Vector(x, y, z));
    }

    @Override
    public void clear(Player observer) {
        int[] ids = new int[frame.getImageGenerator().getLines().length];

        for (int i = 0; i < frame.getImageGenerator().getLines().length; i++) {
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
            if (indices[i] <= frame.getImageGenerator().getLines().length) {
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
                int[] ids = new int[frame.getImageGenerator().getLines().length];

                for (int j = 0; j < frame.getImageGenerator().getLines().length; j++) {
                    ids[j] = j;
                }
                clearTags(p, ids);
            }
            i.remove();
        }
    }

    @Override
    protected void generate(Player observer, int index, double diffY, double x, double y, double z) {
        super.generate(observer, index, diffY, x, y, z);
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
        horse.setDataWatcher(dw.getHandle());

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
}