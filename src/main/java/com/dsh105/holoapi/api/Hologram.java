package com.dsh105.holoapi.api;

import com.dsh105.dshutils.util.ReflectionUtil;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.reflection.SafeField;
import com.dsh105.holoapi.util.ShortIdGenerator;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Hologram {

    private double[] coords = new double[3];
    private String[] tags;
    private double spacing = 0.25D;

    private int id;

    private boolean persistent;
    private String saveId = null;

    protected Hologram(double x, double y, double z, String... lines) {
        this(x, y, z);
        this.tags = lines;
        this.id = ShortIdGenerator.nextId(this.getTagCount());
    }

    protected Hologram(double x, double y, double z, ImageGenerator image) {
        this(x, y, z);
        this.tags = image.getLines();
        this.id = ShortIdGenerator.nextId(this.getTagCount());
    }

    private Hologram(double x, double y, double z) {
        this.coords[0] = x;
        this.coords[1] = y;
        this.coords[2] = z;
    }

    public int getTagCount() {
        return this.tags.length;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public String getSaveId() {
        return saveId;
    }

    public void setSaveId(String saveId) {
        this.saveId = saveId;
        this.persistent = true;
    }

    public double[] getCoords() {
        return coords;
    }

    public String[] getLines() {
        return tags;
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    public int getId() {
        return id;
    }

    public void show(Player observer) {
        for (int index = 0; index < this.getTagCount(); index++) {
            for (Packet packet : this.generate(index, -index * this.spacing)) {
                ReflectionUtil.sendPacket(observer, packet);
            }
        }
    }

    public void move(Player player, Location location) {
        Location loc = location.clone();
        for (int i = 0; i < this.getTagCount(); i++) {
            for (Packet p : this.moveTag(i, loc)) {
                ReflectionUtil.sendPacket(player, p);
            }
            loc.setY(loc.getY() - this.spacing);
        }
    }

    public void remove(Player player) {
        this.clearTags(player);
    }

    public void clearTags(Player observer) {
        int[] ids = new int[this.getTagCount()];

        for (int i = 0; i < this.getTagCount(); i++) {
            ids[i] = i;
        }
        clearTags(observer, ids);
    }

    public void clearTags(Player observer, int... indices) {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy();
        int[] ids = new int[indices.length * 2];

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] <= this.getTagCount()) {
                ids[i * 2] = this.getId() + indices[i] * 2;
                ids[i * 2 + 1] = this.getId() + (indices[i] * 2) * 2 + 1;
            }
        }
        new SafeField<int[]>(destroy.getClass(), "a").set(destroy, ids);
        ReflectionUtil.sendPacket(observer, destroy);
    }

    public Packet[] moveTag(int index, Location to) {
        PacketPlayOutEntityTeleport teleportHorse = new PacketPlayOutEntityTeleport();
        try {
            new SafeField<Integer>(teleportHorse.getClass(), "a").set(teleportHorse, id + index * 2);
            new SafeField<Integer>(teleportHorse.getClass(), "b").set(teleportHorse, to.getBlockX());
            new SafeField<Integer>(teleportHorse.getClass(), "c").set(teleportHorse, to.getBlockY() + 55);
            new SafeField<Integer>(teleportHorse.getClass(), "d").set(teleportHorse, to.getBlockZ());
        } catch (Exception e) {
            e.printStackTrace();
        }

        PacketPlayOutEntityTeleport teleportSkull = new PacketPlayOutEntityTeleport();
        try {
            new SafeField<Integer>(teleportSkull.getClass(), "a").set(teleportSkull, id + index * 2 + 1);
            new SafeField<Integer>(teleportSkull.getClass(), "b").set(teleportSkull, to.getBlockX());
            new SafeField<Integer>(teleportSkull.getClass(), "c").set(teleportSkull, to.getBlockY() + 55);
            new SafeField<Integer>(teleportSkull.getClass(), "d").set(teleportSkull, to.getBlockZ());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Packet[] {teleportHorse, teleportSkull};
    }

    public Packet[] generate(int index, double diffY) {
        PacketPlayOutSpawnEntityLiving horse = new PacketPlayOutSpawnEntityLiving();
        try {
            new SafeField<Integer>(horse.getClass(), "a").set(horse, id + index * 2);
            new SafeField<Integer>(horse.getClass(), "b").set(horse, (int) EntityType.HORSE.getTypeId());
            new SafeField<Integer>(horse.getClass(), "c").set(horse, (int) this.coords[0]);
            new SafeField<Integer>(horse.getClass(), "d").set(horse, (int) (this.coords[1] + diffY + 55));
            new SafeField<Integer>(horse.getClass(), "e").set(horse, (int) this.coords[2]);

            DataWatcher dw = new DataWatcher(null);
            dw.a(10, this.tags[index]);
            dw.a(11, (byte) 1);
            dw.a(12, -1700000);

            new SafeField<DataWatcher>(horse.getClass(), "l").set(horse, dw);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PacketPlayOutSpawnEntity skull = new PacketPlayOutSpawnEntity();
        try {
            new SafeField<Integer>(skull.getClass(), "a").set(skull, id + index * 2 + 1);
            new SafeField<Integer>(skull.getClass(), "b").set(skull, (int) this.coords[0]);
            new SafeField<Integer>(skull.getClass(), "c").set(skull, (int) (this.coords[1] + diffY + 55));
            new SafeField<Integer>(skull.getClass(), "d").set(skull, (int) this.coords[2]);
            new SafeField<Integer>(skull.getClass(), "j").set(skull, 66);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity();
        try {
            new SafeField<Integer>(attach.getClass(), "a").set(attach, new SafeField<Integer>(horse.getClass(), "a").get(horse));
            new SafeField<Integer>(attach.getClass(), "b").set(attach, new SafeField<Integer>(skull.getClass(), "b").get(skull));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Packet[]{horse, skull, attach};
    }

}