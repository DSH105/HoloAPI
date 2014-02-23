package com.dsh105.holoapi.api;

import com.dsh105.dshutils.logger.ConsoleLogger;
import com.dsh105.dshutils.util.ReflectionUtil;
import com.dsh105.holoapi.image.ImageGenerator;
import com.dsh105.holoapi.reflection.SafeField;
import com.dsh105.holoapi.util.ShortIdGenerator;
import com.dsh105.holoapi.util.wrapper.WrapperPacketAttachEntity;
import com.dsh105.holoapi.util.wrapper.WrapperPacketSpawnEntity;
import com.dsh105.holoapi.util.wrapper.WrapperPacketSpawnEntityLiving;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Hologram {

    private double defX;
    private double defY;
    private double defZ;
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
        this.defX = x;
        this.defY = y;
        this.defZ = z;
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

    public double getDefaultX() {
        return defX;
    }

    public double getDefaultY() {
        return defY;
    }

    public double getDefaultZ() {
        return defZ;
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
        this.show(observer, (int) this.getDefaultX(), (int) this.getDefaultY(), (int) this.getDefaultZ());
    }

    public void show(Player observer, Location location) {
        this.show(observer, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void show(Player observer, int x, int y, int z) {
        for (int index = 0; index < this.getTagCount(); index++) {
            this.generate(observer, index, -index * this.spacing, x, y, z);
        }
    }

    public void move(Player observer, Location location) {
        Location loc = location.clone();
        for (int i = 0; i < this.getTagCount(); i++) {
            this.moveTag(observer, i, loc);
            loc.setY(loc.getY() - this.spacing);
        }
    }

    public void clear(Player observer) {
        int[] ids = new int[this.getTagCount()];

        for (int i = 0; i < this.getTagCount(); i++) {
            ids[i] = i;
        }
        clearTags(observer, ids);
    }

    protected void clearTags(Player observer, int... indices) {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy();
        int[] ids = new int[indices.length * 2];

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] <= this.getTagCount()) {
                ids[i * 2] = this.getHorseIndex(indices[i]);
                ids[i * 2 + 1] = this.getSkullIndex(indices[i] * 2);
            }
        }
        new SafeField<int[]>(destroy.getClass(), "a").set(destroy, ids);
        ReflectionUtil.sendPacket(observer, destroy);
    }

    protected void moveTag(Player observer, int index, Location to) {
        PacketPlayOutEntityTeleport teleportHorse = new PacketPlayOutEntityTeleport();
        new SafeField<Integer>(teleportHorse.getClass(), "a").set(teleportHorse, this.getHorseIndex(index));
        new SafeField<Integer>(teleportHorse.getClass(), "b").set(teleportHorse, (int) Math.floor(to.getBlockX() * 32.0D));
        new SafeField<Integer>(teleportHorse.getClass(), "c").set(teleportHorse, (int) Math.floor((to.getBlockY() + 55) * 32.0D));
        new SafeField<Integer>(teleportHorse.getClass(), "d").set(teleportHorse, (int) Math.floor(to.getBlockZ() * 32.0D));

        PacketPlayOutEntityTeleport teleportSkull = new PacketPlayOutEntityTeleport();
        new SafeField<Integer>(teleportSkull.getClass(), "a").set(teleportSkull, this.getSkullIndex(index));
        new SafeField<Integer>(teleportSkull.getClass(), "b").set(teleportSkull, (int) Math.floor(to.getBlockX() * 32.0D));
        new SafeField<Integer>(teleportSkull.getClass(), "c").set(teleportSkull, (int) Math.floor((to.getBlockY() + 55) * 32.0D));
        new SafeField<Integer>(teleportSkull.getClass(), "d").set(teleportSkull, (int) Math.floor(to.getBlockZ() * 32.0D));

        ReflectionUtil.sendPacket(observer, teleportHorse);
        ReflectionUtil.sendPacket(observer, teleportSkull);
    }

    protected void generate(Player observer, int index, double diffY, int x, int y, int z) {
        WrapperPacketAttachEntity attach = new WrapperPacketAttachEntity();

        WrapperPacketSpawnEntityLiving horse = new WrapperPacketSpawnEntityLiving();
        horse.setEntityId(this.getHorseIndex(index));
        horse.setEntityType(EntityType.HORSE.getTypeId());
        horse.setX(x);
        horse.setY(y + diffY + 55);
        horse.setZ(z);

        DataWatcher dw = new DataWatcher(null);
        dw.a(10, this.tags[index]);
        dw.a(11, Byte.valueOf((byte) 1));
        dw.a(12, Integer.valueOf(-1700000));
        horse.setDataWatcher(dw);

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
        /*PacketPlayOutAttachEntity attach = new PacketPlayOutAttachEntity();
        new SafeField<Integer>(attach.getClass(), "b").set(attach, getHorseIndex(index));
        new SafeField<Integer>(attach.getClass(), "c").set(attach, getSkullIndex(index));

        PacketPlayOutSpawnEntityLiving horse = new PacketPlayOutSpawnEntityLiving();
        new SafeField<Integer>(horse.getClass(), "a").set(horse, this.getHorseIndex(index));
        new SafeField<Byte>(horse.getClass(), "b").set(horse, (byte) EntityType.HORSE.getTypeId());
        new SafeField<Integer>(horse.getClass(), "c").set(horse, (int) Math.floor(this.coords[0] * 32.0D));
        new SafeField<Integer>(horse.getClass(), "d").set(horse, (int) Math.floor((this.coords[1] + diffY + 55) * 32.0D));
        new SafeField<Integer>(horse.getClass(), "e").set(horse, (int) Math.floor(this.coords[2] * 32.0D));

        DataWatcher dw = new DataWatcher(null);
        dw.a(10, this.tags[index]);
        dw.a(11, Byte.valueOf((byte) 1));
        dw.a(12, Integer.valueOf(-170000));
        new SafeField<DataWatcher>(horse.getClass(), "l").set(horse, dw);

        PacketPlayOutSpawnEntity skull = new PacketPlayOutSpawnEntity();
        new SafeField<Integer>(skull.getClass(), "a").set(skull, this.getSkullIndex(index));
        new SafeField<Integer>(skull.getClass(), "b").set(skull, (int) Math.floor(this.coords[0] * 32.0D));
        new SafeField<Integer>(skull.getClass(), "c").set(skull, (int) Math.floor((this.coords[1] + diffY + 55) * 32.0D));
        new SafeField<Integer>(skull.getClass(), "d").set(skull, (int) Math.floor(this.coords[2] * 32.0D));
        new SafeField<Byte>(skull.getClass(), "j").set(skull, (byte) 66); // From EntityTrackerEntry

        ReflectionUtil.sendPacket(observer, horse);
        ReflectionUtil.sendPacket(observer, skull);
        ReflectionUtil.sendPacket(observer, attach);*/
    }

    private int getHorseIndex(int index) {
        return id + index * 2;
    }

    private int getSkullIndex(int index) {
        return id + index * 2 + 1;
    }

}
