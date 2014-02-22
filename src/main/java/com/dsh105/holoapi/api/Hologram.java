package com.dsh105.holoapi.api;

import com.dsh105.dshutils.util.ReflectionUtil;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.util.ShortIdGenerator;
import net.minecraft.server.v1_7_R1.DataWatcher;
import net.minecraft.server.v1_7_R1.Packet;
import net.minecraft.server.v1_7_R1.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Hologram {

    private double[] coords = new double[3];
    private String[] tags;
    private double spacing = 0.25D;

    private int id;

    public Hologram(double x, double y, double z, String... lines) {
        this(x, y, z);
        this.tags = lines;
        this.id = ShortIdGenerator.nextId(this.tags.length);
    }

    public Hologram(double x, double y, double z, ImageGenerator image) {
        this(x, y, z);
        this.tags = image.getLines();
        this.id = ShortIdGenerator.nextId(this.tags.length);
    }

    protected Hologram(double x, double y, double z) {
        this.coords[0] = x;
        this.coords[1] = y;
        this.coords[2] = z;
    }

    public void show(Player observer) {
        for (int index = 0; index < this.tags.length; index++) {
            for (Packet packet : this.generate(index, -index * this.spacing)) {
                ReflectionUtil.sendPacket(observer, packet);
            }
        }
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