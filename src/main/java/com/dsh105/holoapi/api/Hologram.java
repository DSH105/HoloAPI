package com.dsh105.holoapi.api;

import com.dsh105.dshutils.util.ReflectionUtil;
import com.dsh105.holoapi.image.ImageGenerator;
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
        try {
            ReflectionUtil.setValue(destroy, "a", ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ReflectionUtil.sendPacket(observer, destroy);
    }

    public Packet[] moveTag(int index, Location to) {
        PacketPlayOutEntityTeleport teleportHorse = new PacketPlayOutEntityTeleport();
        try {
            ReflectionUtil.setValue(teleportHorse, "a", id + index * 2);
            ReflectionUtil.setValue(teleportHorse, "b", to.getX());
            ReflectionUtil.setValue(teleportHorse, "c", to.getY() + 55);
            ReflectionUtil.setValue(teleportHorse, "d", to.getZ());
        } catch (Exception e) {
            e.printStackTrace();
        }

        PacketPlayOutEntityTeleport teleportSkull = new PacketPlayOutEntityTeleport();
        try {
            ReflectionUtil.setValue(teleportSkull, "a", id + index * 2 + 1);
            ReflectionUtil.setValue(teleportHorse, "b", to.getX());
            ReflectionUtil.setValue(teleportHorse, "c", to.getY() + 55);
            ReflectionUtil.setValue(teleportHorse, "d", to.getZ());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Packet[] {teleportHorse, teleportSkull};
    }

    public Packet[] generate(int index, double diffY) {
        PacketPlayOutSpawnEntityLiving horse = new PacketPlayOutSpawnEntityLiving();
        try {
            ReflectionUtil.setValue(horse, "a", id + index * 2);
            ReflectionUtil.setValue(horse, "b", EntityType.HORSE.getTypeId());
            ReflectionUtil.setValue(horse, "c", this.coords[0]);
            ReflectionUtil.setValue(horse, "d", this.coords[1] + diffY + 55);
            ReflectionUtil.setValue(horse, "e", this.coords[2]);

            DataWatcher dw = new DataWatcher(null);
            dw.a(10, this.tags[index]);
            dw.a(11, (byte) 1);
            dw.a(12, -1700000);

            ReflectionUtil.setValue(horse, "l", dw);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PacketPlayOutAttachEntity skull = new PacketPlayOutAttachEntity();
        try {
            ReflectionUtil.setValue(skull, "a", id + index * 2 + 1);
            ReflectionUtil.setValue(skull, "b", this.coords[0]);
            ReflectionUtil.setValue(skull, "c", this.coords[1] + diffY + 55);
            ReflectionUtil.setValue(skull, "d", this.coords[2]);
            ReflectionUtil.setValue(skull, "j", 66); // Wither Skull ID thingy
        } catch (Exception e) {
            e.printStackTrace();
        }

        PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity();
        try {
            ReflectionUtil.setValue(attach, "a", ReflectionUtil.getDeclaredField(horse.getClass(), "a").get(horse));
            ReflectionUtil.setValue(attach, "b", ReflectionUtil.getDeclaredField(skull.getClass(), "a").get(skull));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Packet[]{horse, skull, attach};
    }

}